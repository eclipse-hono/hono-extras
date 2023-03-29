/*
 * ***********************************************************
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  <p>
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *  <p>
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *  <p>
 *  SPDX-License-Identifier: EPL-2.0
 * **********************************************************
 *
 */

package org.eclipse.hono.communication.api.service.communication;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.client.pubsub.PubSubBasedAdminClientManager;
import org.eclipse.hono.client.pubsub.PubSubMessageHelper;
import org.eclipse.hono.communication.api.config.PubSubConstants;
import org.eclipse.hono.communication.api.handler.ConfigTopicEventHandler;
import org.eclipse.hono.communication.api.handler.StateTopicEventHandler;
import org.eclipse.hono.communication.api.repository.DeviceRepository;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.eclipse.hono.notification.deviceregistry.LifecycleChange;
import org.eclipse.hono.notification.deviceregistry.TenantChangeNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.common.base.Strings;
import com.google.pubsub.v1.PubsubMessage;

import io.vertx.core.Vertx;

/**
 * Internal topic manager interface.
 */
@ApplicationScoped
public class InternalTopicManagerImpl implements InternalTopicManager {

    static ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(InternalTopicManagerImpl.class);
    private final DeviceRepository deviceRepository;
    private final ConfigTopicEventHandler configTopicEventHandler;
    private final StateTopicEventHandler stateTopicEventHandler;
    private final InternalMessaging internalMessaging;
    private final InternalMessagingConfig internalMessagingConfig;
    private final Vertx vertx;

    /**
     * Creates a new InternalTopicManagerImpl.
     *
     * @param deviceRepository          The device repository
     * @param configTopicEventHandler   The config topic event handler.
     * @param stateTopicEventHandler    The state topic event handler.
     * @param internalMessaging         The internal messaging interface.
     * @param internalMessagingConfig   The internal messaging config.
     * @param vertx                     The Vertx instance to use
     */
    public InternalTopicManagerImpl(final DeviceRepository deviceRepository,
            final ConfigTopicEventHandler configTopicEventHandler,
            final StateTopicEventHandler stateTopicEventHandler, final InternalMessaging internalMessaging,
            final InternalMessagingConfig internalMessagingConfig, final Vertx vertx) {
        this.deviceRepository = deviceRepository;
        this.configTopicEventHandler = configTopicEventHandler;
        this.stateTopicEventHandler = stateTopicEventHandler;
        this.internalMessaging = internalMessaging;
        this.internalMessagingConfig = internalMessagingConfig;
        this.vertx = vertx;
    }

    @Override
    public void initPubSubTopicsAndSubscriptions() {
        deviceRepository.listDistinctTenants()
                .onSuccess(this::createTopicsAndSubscribeToEvents)
                .onFailure(err -> log.error("Error by creating and  subscribing to topics: {}", err.getMessage()));
        internalMessaging.subscribe(PubSubConstants.TENANT_NOTIFICATIONS, this::onTenantChanges);
    }

    private void createTopicsAndSubscribeToEvents(final List<String> tenants) {

        final var context = Vertx.currentContext();
        context.executeBlocking(promise -> {
            tenants.forEach(this::createPubSubTopicsAndSubscriptions);
            tenants.forEach(tenant -> internalMessaging.subscribe(
                    internalMessagingConfig.getEventTopicFormat().formatted(tenant),
                    configTopicEventHandler::onDeviceConnectEvent));
            tenants.forEach(tenant -> internalMessaging.subscribe(
                    internalMessagingConfig.getStateTopicFormat().formatted(tenant),
                    stateTopicEventHandler::onStateMessage));
            promise.complete();
        });
    }

    /**
     * Creates Pub/Sub topics and subscriptions for the provided tenant.
     *
     * @param tenantId The tenant.
     */
    private void createPubSubTopicsAndSubscriptions(final String tenantId) {
        final String projectId = internalMessagingConfig.getProjectId();
        final FixedCredentialsProvider credentialsProvider;
        if (PubSubMessageHelper.getCredentialsProvider().isEmpty() || projectId == null) {
            log.error("Either credentials provider is empty: {} or project id ({}) is null.", PubSubMessageHelper.getCredentialsProvider().isEmpty(), projectId);
            return;
        }
        credentialsProvider = PubSubMessageHelper.getCredentialsProvider().get();

        final List<String> topics = PubSubConstants.getTopicsToCreate();

        topics.forEach(topic -> {
            final var pubSubBasedTopicManager = new PubSubBasedAdminClientManager(projectId, credentialsProvider, vertx);
            pubSubBasedTopicManager.getOrCreateTopicAndSubscription(topic, tenantId);
            pubSubBasedTopicManager.closeAdminClientsBlocking();

        });
        log.info("All Topics created for {}", tenantId);

    }

    /**
     * Handle incoming tenant CREATE notifications.
     *
     * @param pubsubMessage The message to handle
     * @param consumer The message consumer
     */
    public void onTenantChanges(final PubsubMessage pubsubMessage, final AckReplyConsumer consumer) {
        consumer.ack();
        final String jsonString = pubsubMessage.getData().toStringUtf8();
        final TenantChangeNotification notification;
        log.debug("Handle tenant change notification {}", jsonString);
        try {
            notification = objectMapper.readValue(jsonString, TenantChangeNotification.class);
        } catch (JsonProcessingException e) {
            log.error("Can't deserialize tenant change notification: {}", e.getMessage());
            return;
        }
        final String tenant = notification.getTenantId();
        if (notification.getChange() == LifecycleChange.CREATE && !Strings.isNullOrEmpty(tenant)) {
            createPubSubTopicsAndSubscriptions(tenant);
            internalMessaging.subscribe(internalMessagingConfig.getEventTopicFormat().formatted(tenant),
                    configTopicEventHandler::onDeviceConnectEvent);
            internalMessaging.subscribe(internalMessagingConfig.getStateTopicFormat().formatted(tenant),
                    stateTopicEventHandler::onStateMessage);
        }
    }
}
