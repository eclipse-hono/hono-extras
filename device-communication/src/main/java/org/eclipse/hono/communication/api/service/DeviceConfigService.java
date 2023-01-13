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

package org.eclipse.hono.communication.api.service;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import org.eclipse.hono.communication.api.entity.DeviceConfigEntity;
import org.eclipse.hono.communication.api.entity.DeviceConfigRequest;
import org.eclipse.hono.communication.api.entity.ListDeviceConfigVersionsResponse;

/**
 * Device config interface
 */
public interface DeviceConfigService {

    Future<@Nullable DeviceConfigEntity> modifyCloudToDeviceConfig(DeviceConfigRequest deviceConfig, String deviceId, String tenantId);

    Future<ListDeviceConfigVersionsResponse> listAll(String deviceId, String tenantId, int limit);
}
