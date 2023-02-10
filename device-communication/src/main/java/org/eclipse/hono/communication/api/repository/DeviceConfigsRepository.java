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

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlConnection;
import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;

import java.util.List;

/**
 * Device config repository interface
 */
public interface DeviceConfigsRepository {

    Future<List<DeviceConfig>> listAll(SqlConnection sqlConnection, String deviceId, String tenantId, int limit);

    Future<DeviceConfigEntity> createNew(SqlConnection sqlConnection, DeviceConfigEntity entity);
}
