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

import org.eclipse.hono.communication.api.data.DeviceState;
import org.eclipse.hono.communication.api.data.DeviceStateEntity;

import io.vertx.core.Future;

/**
 * Device state repository interface.
 */
public interface DeviceStateRepository {

    /**
     * Lists all states for a specific device. Result is ordered by version desc
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @param limit    The number of states to show
     * @return A Future with a List of DeviceStates
     */
    Future<List<DeviceState>> listAll(String deviceId, String tenantId, int limit);

    /**
     * Creates a new state and deletes the oldest one if the total number of versions in the DB is bigger than the MAX_LIMIT.
     *
     * @param entity The instance to insert.
     * @return A Future of the created DeviceStateEntity.
     */
    Future<DeviceStateEntity> createNew(DeviceStateEntity entity);
}
