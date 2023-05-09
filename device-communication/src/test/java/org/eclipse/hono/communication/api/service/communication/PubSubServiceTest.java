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

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;


import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;


class PubSubServiceTest {

    private static final String PROJECT_ID = "your-project-id";
    private final String topic = "my-topic";
    private final byte[] message = "Hello world!".getBytes();
    private final Map<String, String> attributes = new HashMap<>();
    private InternalMessagingConfig configMock;
    private Publisher.Builder publisherBuilderMock;
    private Publisher publisherMock;
    private Subscriber.Builder subscriberBuilderMock;
    private Subscriber subscriberMock;
    private MessageReceiver messageReceiverMock;

    @BeforeEach
    void setUp() {
        configMock = mock(InternalMessagingConfig.class);
        publisherBuilderMock = mock(Publisher.Builder.class);
        publisherMock = mock(Publisher.class);
        subscriberBuilderMock = mock(Subscriber.Builder.class);
        subscriberMock = mock(Subscriber.class);
        messageReceiverMock = mock(MessageReceiver.class);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(configMock, publisherBuilderMock, publisherMock, subscriberBuilderMock, subscriberMock, messageReceiverMock);
    }

    @Test
    void testPublishSuccessful() throws Exception {

        attributes.put("key1", "value1");

        try (MockedStatic<Publisher> publisherMockedStatic = mockStatic(Publisher.class)) {

            final Publisher.Builder builderMock = mock(Publisher.Builder.class);

            final ApiFuture<String> mockedApiFuture = mock(ApiFuture.class);
            publisherMockedStatic.when(() -> Publisher.newBuilder(ArgumentMatchers.any(TopicName.class))).thenReturn(builderMock);

            when(builderMock.build()).thenReturn(publisherMock);
            when(publisherMock.publish(ArgumentMatchers.any(PubsubMessage.class))).thenReturn(mockedApiFuture);
            when(mockedApiFuture.get()).thenReturn("message-id");
            when(configMock.getProjectId()).thenReturn(PROJECT_ID);

            final PubSubService pubSubService = new PubSubService(configMock);

            // When
            pubSubService.publish(topic, message, attributes);

            // Then
            verify(configMock).getProjectId();
            verify(publisherMock).shutdown();
            verify(publisherMock).publish(ArgumentMatchers.any(PubsubMessage.class));
            verify(publisherMock).awaitTermination(1, TimeUnit.MINUTES);
            publisherMockedStatic.verify(() -> Publisher.newBuilder(ArgumentMatchers.any(TopicName.class)));
            publisherMockedStatic.verifyNoMoreInteractions();
        }

    }

    @Test
    void testPublish_failed_null_pubSUb_message() throws Exception {
        attributes.put("key1", "value1");

        try (MockedStatic<Publisher> publisherMockedStatic = mockStatic(Publisher.class)) {
            try (MockedStatic<PubsubMessage> pubsubMessageMockedStatic = mockStatic(PubsubMessage.class)) {


                final Publisher.Builder builderMock = mock(Publisher.Builder.class);

                final ApiFuture<String> mockedApiFuture = mock(ApiFuture.class);
                publisherMockedStatic.when(() -> Publisher.newBuilder(ArgumentMatchers.any(TopicName.class))).thenReturn(builderMock);

                when(builderMock.build()).thenReturn(publisherMock);
                when(publisherMock.publish(ArgumentMatchers.any(PubsubMessage.class))).thenReturn(mockedApiFuture);
                when(mockedApiFuture.get()).thenReturn("message-id");
                when(configMock.getProjectId()).thenReturn(PROJECT_ID);

                final PubSubService pubSubService = new PubSubService(configMock);

                pubSubService.publish(topic, message, attributes);

                verify(configMock).getProjectId();
                verify(publisherMock).shutdown();
                verify(publisherMock).awaitTermination(1, TimeUnit.MINUTES);
                publisherMockedStatic.verify(() -> Publisher.newBuilder(ArgumentMatchers.any(TopicName.class)));
                pubsubMessageMockedStatic.verify(PubsubMessage::newBuilder);
                publisherMockedStatic.verifyNoMoreInteractions();
                pubsubMessageMockedStatic.verifyNoMoreInteractions();
            }
        }

    }


    @Test
    public void testSubscribe_success() throws Exception {
        final String subscriptionName = "my-sub";
        final ProjectSubscriptionName projectSubscriptionName = ProjectSubscriptionName.of(PROJECT_ID, subscriptionName);
        try (MockedStatic<Subscriber> subscriberMockedStatic = mockStatic(Subscriber.class)) {
            subscriberMockedStatic.when(() -> Subscriber.newBuilder(projectSubscriptionName, messageReceiverMock)).thenReturn(subscriberBuilderMock);
            when(configMock.getProjectId()).thenReturn(PROJECT_ID);
            when(subscriberBuilderMock.build()).thenReturn(subscriberMock);
            final PubSubService pubSubServiceSpyClient = spy(new PubSubService(configMock));
            doReturn(projectSubscriptionName).when(pubSubServiceSpyClient).initSubscription(topic);


            pubSubServiceSpyClient.subscribe(topic, messageReceiverMock);

            verify(configMock).getProjectId();
            verify(subscriberBuilderMock).build();
            verify(pubSubServiceSpyClient, times(1)).initSubscription(topic);
            subscriberMockedStatic.verify(() -> Subscriber.newBuilder(projectSubscriptionName, messageReceiverMock));
            verify(subscriberMock, times(1)).startAsync();
            verify(pubSubServiceSpyClient).subscribe(topic, messageReceiverMock);
            verifyNoMoreInteractions(pubSubServiceSpyClient, subscriberMock);


            subscriberMockedStatic.verifyNoMoreInteractions();


        }

    }

    @Test
    public void testSubscribe_failed() throws Exception {
        final String subscriptionName = "my-sub";
        final ProjectSubscriptionName projectSubscriptionName = ProjectSubscriptionName.of(PROJECT_ID, subscriptionName);
        try (MockedStatic<Subscriber> subscriberMockedStatic = mockStatic(Subscriber.class)) {
            subscriberMockedStatic.when(() -> Subscriber.newBuilder(projectSubscriptionName, messageReceiverMock)).thenReturn(subscriberBuilderMock);
            when(configMock.getProjectId()).thenReturn(PROJECT_ID);
            when(subscriberBuilderMock.build()).thenReturn(subscriberMock);
            final PubSubService pubSubServiceSpyClient = spy(new PubSubService(configMock));
            doThrow(new IOException()).when(pubSubServiceSpyClient).initSubscription(topic);


            pubSubServiceSpyClient.subscribe(topic, messageReceiverMock);


            verify(configMock, times(1)).getProjectId();
            verify(pubSubServiceSpyClient, times(1)).initSubscription(topic);
            verify(pubSubServiceSpyClient, times(1)).subscribe(topic, messageReceiverMock);
            verifyNoMoreInteractions(pubSubServiceSpyClient, subscriberMock);


            subscriberMockedStatic.verifyNoMoreInteractions();
        }

    }


}
