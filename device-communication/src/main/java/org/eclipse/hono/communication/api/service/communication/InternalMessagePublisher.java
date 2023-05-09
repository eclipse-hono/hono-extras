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

import java.util.Map;

/**
 * Interface for internal communication topic publisher.
 */
public interface InternalMessagePublisher {

    /**
     * Publish a message to a topic.
     *
     * @param topic      The topic to publish the message
     * @param message    The message to publish
     * @param attributes The message attributes
     * @throws Exception Throws Exception if subscription can't be created
     */
    void publish(String topic, byte[] message, Map<String, String> attributes) throws Exception;
}
