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

package org.eclipse.hono.communication.api.service.state;

import javax.inject.Singleton;

import org.eclipse.hono.communication.api.mapper.DeviceStateMapper;
import org.eclipse.hono.communication.api.repository.DeviceStateRepository;
import org.eclipse.hono.communication.api.service.DeviceServiceAbstract;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.common.base.Strings;
import com.google.pubsub.v1.PubsubMessage;

/**
 * Service for device commands.
 */

@Singleton
public class DeviceStateServiceImpl extends DeviceServiceAbstract implements DeviceStateService {

    private final Logger log = LoggerFactory.getLogger(DeviceStateServiceImpl.class);
    private final DeviceStateRepository repository;
    private final DeviceStateMapper mapper;

    /**
     * Creates a new DeviceStateServiceImpl.
     *
     * @param repository              The device state repository
     * @param mapper                  The device state mapper
     * @param internalMessagingConfig The internal messaging config
     * @param internalMessaging       The internal messaging interface
     */
    protected DeviceStateServiceImpl(final DeviceStateRepository repository, final DeviceStateMapper mapper,
            final InternalMessagingConfig internalMessagingConfig, final InternalMessaging internalMessaging) {
        super(internalMessagingConfig, internalMessaging);
        this.repository = repository;
        this.mapper = mapper;
        subscribeToAllStateTopics();
    }

    /**
     * Subscribe to all tenant state topics.
     */
    public void subscribeToAllStateTopics() {
        repository.listTenants()
                .onSuccess(tenants -> tenants
                        .forEach(tenant -> {
                            final var topic = messagingConfig.getStateTopicFormat().formatted(tenant);
                            internalMessaging.subscribe(topic, this::onStateMessage);

                        }))
                .onFailure(err -> log.error("Error subscribing to all state topics: {}", err.getMessage()));
    }

    /**
     * Handle incoming messages on state topics.
     *
     * @param pubsubMessage The message to handle.
     * @param consumer The message consumer.
     */
    public void onStateMessage(final PubsubMessage pubsubMessage, final AckReplyConsumer consumer) {

        consumer.ack(); // message was received and processed only once

        final String msg = pubsubMessage.getData().toStringUtf8();

        if (Strings.isNullOrEmpty(msg)) {
            log.debug("Skip state: payload is empty");
            return;
        }

        repository.createNew(mapper.pubSubMessageToDeviceStateEntity(pubsubMessage))
                .onFailure(err -> log.error("Can't save state in DB: {}", err.getMessage()));
    }
}
