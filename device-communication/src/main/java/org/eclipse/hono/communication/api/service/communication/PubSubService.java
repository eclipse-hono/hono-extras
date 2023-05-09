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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.TopicName;

/**
 * Internal messaging interface implementation.
 */
@ApplicationScoped
public class PubSubService implements InternalMessaging {

    public static final String COMMUNICATION_API_SUBSCRIPTION_NAME = "%s-communication-api";
    private final Logger log = LoggerFactory.getLogger(PubSubService.class);
    private final Map<String, Subscriber> activeSubscriptions = new HashMap<>();

    private final String projectId;
    private TopicName topicName;


    /**
     * Creates a new PubSubService.
     *
     * @param configs The internal messaging configs
     */
    public PubSubService(final InternalMessagingConfig configs) {
        this.projectId = configs.getProjectId();
    }

    /**
     * Stops every subscription at destroy time.
     */
    @PreDestroy
    void destroy() {

        activeSubscriptions.forEach((topic, subscriber) -> {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        });

        activeSubscriptions.clear();
    }

    @Override
    public void publish(final String topic, final byte[] message, final Map<String, String> attributes) throws Exception {
        final Publisher publisher = Publisher.newBuilder(TopicName.of(projectId, topic))
                .build();
        try {
            final var data = ByteString.copyFrom(message);
            final var pubsubMessage = PubsubMessage
                    .newBuilder()
                    .setData(data)
                    .putAllAttributes(attributes)
                    .build();
            final ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<>() {
                public void onSuccess(final String messageId) {
                    log.debug("Message was published with id {}", messageId);
                }

                public void onFailure(final Throwable t) {
                    log.error("failed to publish: {}", t.getMessage());
                }
            }, MoreExecutors.directExecutor());
        } catch (Exception ex) {
            log.error(String.format("Error publish to topic %s: %s", topic, ex.getMessage()));
        } finally {
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
    }


    @Override
    public void subscribe(final String topic, final MessageReceiver callbackHandler) {
        if (activeSubscriptions.containsKey(topic)) {
            return;
        }
        topicName = TopicName.of(projectId, topic);
        final ProjectSubscriptionName subscriptionName;
        try {
            subscriptionName = initSubscription(topic);
            final Subscriber subscriber = Subscriber.newBuilder(subscriptionName, callbackHandler).build();
            subscriber.startAsync().awaitRunning();
            activeSubscriptions.put(topic, subscriber);
            log.info("Successfully subscribe to topic: {}", topicName.getTopic());
        } catch (Exception ex) {
            log.error("Error subscribe to topic {}: {}", topic, ex.getMessage());
        }
    }

    /**
     * If the subscription doesn't exist creates a new one.
     *
     * @param topic Topic name, it will be used for creating the subscription topic_name-sub
     * @return The ProjectSubscriptionName object
     * @throws IOException if subscription can't be created
     */
    ProjectSubscriptionName initSubscription(final String topic) throws IOException {
        final var subscriptionName = ProjectSubscriptionName.of(
                projectId,
                String.format(COMMUNICATION_API_SUBSCRIPTION_NAME, topic)
        );
        final var subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .build();
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings)) {
            final var subscriptions = subscriptionAdminClient.listSubscriptions(ProjectName.of(projectId))
                    .iterateAll();
            final Optional<Subscription> existing = StreamSupport
                    .stream(subscriptions.spliterator(), false)
                    .filter(sub -> sub.getName().equals(subscriptionName.toString()))
                    .findFirst();

            if (existing.isEmpty()) {

                subscriptionAdminClient.createSubscription(
                        subscriptionName.toString(),
                        topicName,
                        PushConfig.getDefaultInstance(),
                        50
                );
            }
        }
        return subscriptionName;
    }
}
