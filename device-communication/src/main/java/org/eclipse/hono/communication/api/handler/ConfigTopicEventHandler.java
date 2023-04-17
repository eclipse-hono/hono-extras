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

package org.eclipse.hono.communication.api.handler;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.pubsub.v1.PubsubMessage;

/**
 * Config topic event handler interface.
 */
public interface ConfigTopicEventHandler {

    /**
     * Handle incoming empty notification events.
     *
     * @param pubsubMessage The message to handle
     * @param consumer      The message consumer
     */
    void onDeviceConnectEvent(PubsubMessage pubsubMessage, AckReplyConsumer consumer);

    /**
     * Handle incoming ack config message.
     *
     * @param pubsubMessage The message to handle
     * @param consumer      The message consumer
     */

    void onDeviceConfigAck(PubsubMessage pubsubMessage, AckReplyConsumer consumer);
}