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

package org.eclipse.hono.communication.api.mapper;

import java.time.Instant;

import org.eclipse.hono.communication.api.data.DeviceStateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.google.pubsub.v1.PubsubMessage;

/**
 * Mapper for device state objects.
 */
@Mapper(componentModel = "cdi", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceStateMapper {

    /**
     * Convert Pub/Sub message to device state entity.
     *
     * @param pubsubMessage The Pub/Sub message.
     * @return The device state entity.
     */
    @Mapping(target = "version", source = "request.versionToUpdate")
    @Mapping(target = "cloudUpdateTime", expression = "java(getDateTime())")
    @Mapping(target = "tenantId", source = "pubsubMessage.getAttributesMap().get(\"tenantId\")")
    @Mapping(target = "deviceId", source = "pubsubMessage.getAttributesMap().get(\"deviceId\")")
    @Mapping(target = "binaryData", source = "pubsubMessage.getData()")
    DeviceStateEntity pubSubMessageToDeviceStateEntity(PubsubMessage pubsubMessage);

    default String getDateTime() {
        return Instant.now().toString();
    }
}
