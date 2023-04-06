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

package org.eclipse.hono.communication.api.repository;

import java.util.List;

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;

import io.vertx.core.Future;

/**
 * Device config repository interface.
 */
public interface DeviceConfigRepository {

    /**
     * Lists all config versions for a specific device. Result is order by version desc
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @param limit    The number of config to show
     * @return A Future with a List of DeviceConfigs
     */
    Future<List<DeviceConfig>> listAll(String deviceId, String tenantId, int limit);


    /**
     * Creates a new config version and deletes the oldest version if the total num of versions in DB is bigger than the MAX_LIMIT.
     *
     * @param entity The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    Future<DeviceConfigEntity> createNew(DeviceConfigEntity entity);

    /**
     * Update the deviceAckTime field.
     *
     * @param ack           The acknowledgment object
     * @param deviceAckTime The ack Time
     * @return Future of Void
     */

    Future<Void> updateDeviceAckTime(DeviceConfigAckResponse ack, String deviceAckTime);


    /**
     * Get device latest config max(version).
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @return Future of DeviceConfigEntity
     */
    Future<DeviceConfigEntity> getDeviceLatestConfig(String deviceId, String tenantId);
}
