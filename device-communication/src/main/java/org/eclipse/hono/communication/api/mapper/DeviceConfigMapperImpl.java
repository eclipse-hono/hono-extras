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


import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;

/**
 * Implementation for mapper for device config objects.
 */
@ApplicationScoped
public class DeviceConfigMapperImpl implements DeviceConfigMapper {

    @Override
    public DeviceConfig deviceConfigEntityToConfig(final DeviceConfigEntity entity) {
        if (entity == null) {
            return null;
        }

        final DeviceConfig deviceConfig = new DeviceConfig();

        deviceConfig.setVersion(String.valueOf(entity.getVersion()));
        deviceConfig.setCloudUpdateTime(entity.getCloudUpdateTime());
        deviceConfig.setDeviceAckTime(entity.getDeviceAckTime());
        deviceConfig.setBinaryData(entity.getBinaryData());

        return deviceConfig;
    }

    @Override
    public DeviceConfigEntity configRequestToDeviceConfigEntity(final DeviceConfigRequest request) {
        if (request == null) {
            return null;
        }

        final DeviceConfigEntity deviceConfigEntity = new DeviceConfigEntity();

        if (request.getVersionToUpdate() != null) {
            deviceConfigEntity.setVersion(Integer.parseInt(request.getVersionToUpdate()));
        }
        deviceConfigEntity.setBinaryData(request.getBinaryData());

        deviceConfigEntity.setCloudUpdateTime(getDateTime());

        return deviceConfigEntity;
    }
}
