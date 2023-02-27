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

import org.eclipse.hono.communication.api.data.DeviceStateEntity;

import io.vertx.core.Future;

/**
 * Device state repository interface.
 */
public interface DeviceStateRepository {

    /**
     * List all distinct tenant ID's from device registrations table.
     *
     * @return Future of List with all tenants.
     */
    Future<List<String>> listTenants();

    /**
     * Creates a new state in the DB.
     *
     * @param entity The instance to insert.
     * @return A Future of the created DeviceStateEntity.
     */
    Future<DeviceStateEntity> createNew(DeviceStateEntity entity);
}
