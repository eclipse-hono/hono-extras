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

import org.eclipse.hono.communication.api.entity.DeviceConfig;
import org.eclipse.hono.communication.api.entity.DeviceConfigEntity;
import org.eclipse.hono.communication.api.entity.DeviceConfigRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;

@Mapper(componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceConfigMapper {

    DeviceConfigEntity deviceConfigToEntity(DeviceConfig deviceConfig);


    DeviceConfig entityToDeviceConfig(DeviceConfigEntity entity);

    @Mapping(target = "version", source = "request.versionToUpdate")
    @Mapping(target = "cloudUpdateTime", expression = "java(getDateTime())")
    DeviceConfig configRequestToDeviceConfig(DeviceConfigRequest request);

    @Mapping(target = "version", source = "request.versionToUpdate")
    @Mapping(target = "cloudUpdateTime", expression = "java(getDateTime())")
    DeviceConfigEntity configRequestToDeviceConfigEntity(DeviceConfigRequest request);

    default String getDateTime() {
        return Instant.now().toString();
    }
}
