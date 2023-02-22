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

package org.eclipse.hono.communication.api.service.config;

import io.vertx.core.Future;
import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;

/**
 * Device config interface
 */
public interface DeviceConfigService {

    Future<DeviceConfig> modifyCloudToDeviceConfig(DeviceConfigRequest deviceConfig, String deviceId, String tenantId);

    Future<ListDeviceConfigVersionsResponse> listAll(String deviceId, String tenantId, int limit);

    void updateDeviceAckTime(DeviceConfigAckResponse configAckResponse, String deviceAckTime);
}
