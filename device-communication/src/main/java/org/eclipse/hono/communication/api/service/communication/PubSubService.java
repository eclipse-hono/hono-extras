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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;


/**
 * InternalMessaging implementation
 */
@ApplicationScoped
public class PubSubService implements InternalMessaging {

    private final Logger log = LoggerFactory.getLogger(PubSubService.class);
    private final Map<String, Subscriber> activeSubscriptions = new HashMap<>();

    private final String projectId;
    private TopicName topicName;


    public PubSubService(InternalMessagingConfig configs) {
        this.projectId = configs.getProjectId();
    }

    /**
     * Stops every subscription at destroy time
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


    /**
     * Publish a message to a topic
     *
     * @param topic   The topic to publish the message
     * @param message The message to publish
     * @throws Exception Throws Exception if subscription can't be created
     */
    @Override
    public void publish(String topic, String message, Map<String, String> attributes) throws Exception {
        Publisher publisher = Publisher.newBuilder(TopicName.of(projectId, topic))
                .build();
        try {
            var data = ByteString.copyFromUtf8(message);
            var pubsubMessage = PubsubMessage
                    .newBuilder()
                    .setData(data)
                    .putAllAttributes(attributes)
                    .build();
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<>() {
                public void onSuccess(String messageId) {
                    log.debug("Message was published with id {}", messageId);
                }

                public void onFailure(Throwable t) {
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

    /**
     * Subscribe to a topic
     *
     * @param topic           The topic to subscribe
     * @param callbackHandler The function to be called when a message is received
     */
    @Override
    public void subscribe(String topic, MessageReceiver callbackHandler) {
        if (activeSubscriptions.containsKey(topic)) {
            return;
        }
        topicName = TopicName.of(projectId, topic);
        ProjectSubscriptionName subscriptionName;
        try {
            subscriptionName = initSubscription(topic);
            Subscriber subscriber = Subscriber.newBuilder(subscriptionName, callbackHandler).build();
            subscriber.startAsync().awaitRunning();
            activeSubscriptions.put(topic, subscriber);
            log.info("Successfully subscribe to topic: {}", topicName.getTopic());
        } catch (IOException ex) {
            log.error("Error subscribe to topic {}: {}", topic, ex.getMessage());
        }
    }

    /**
     * If the subscription doesn't exist creates a new one
     *
     * @param topic Topic name, it will be used for creating the subscription <topic_name>-sub
     * @return The ProjectSubscriptionName object
     * @throws IOException if subscription can't be created
     */
    private ProjectSubscriptionName initSubscription(String topic) throws IOException {
        var subscriptionName = ProjectSubscriptionName.of(
                projectId,
                String.format("%s-sub", topic)
        );
        var subscriptionAdminSettings = SubscriptionAdminSettings.newBuilder()
                .build();
        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(subscriptionAdminSettings)) {
            var subscriptions = subscriptionAdminClient.listSubscriptions(ProjectName.of(projectId))
                    .iterateAll();
            Optional<Subscription> existing = StreamSupport
                    .stream(subscriptions.spliterator(), false)
                    .filter(sub -> sub.getName().equals(subscriptionName.toString()))
                    .findFirst();

            if (existing.isEmpty()) {

                subscriptionAdminClient.createSubscription(
                        subscriptionName,
                        topicName,
                        PushConfig.getDefaultInstance(),
                        0
                );
            }
        }
        return subscriptionName;
    }

}
