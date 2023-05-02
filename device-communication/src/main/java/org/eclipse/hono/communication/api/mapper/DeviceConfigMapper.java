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

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for device config objects.
 */
@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceConfigMapper {


    /**
     * Convert device config entity to device config.
     *
     * @param entity The device config entity
     * @return The device config
     */
    DeviceConfig deviceConfigEntityToConfig(DeviceConfigEntity entity);

    /**
     * Convert device config request to device config entity.
     *
     * @param request The device config request
     * @return The device config entity
     */
    @Mapping(target = "version", source = "request.versionToUpdate")
    @Mapping(target = "cloudUpdateTime", expression = "java(getDateTime())")
    DeviceConfigEntity configRequestToDeviceConfigEntity(DeviceConfigRequest request);

    default String getDateTime() {
        return Instant.now().toString();
    }
}
