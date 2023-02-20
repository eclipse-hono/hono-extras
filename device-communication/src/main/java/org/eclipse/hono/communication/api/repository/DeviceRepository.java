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
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.eclipse.hono.communication.core.app.DatabaseConfig;

import java.util.Map;

public interface DeviceRepository {
    String SQL_COUNT_DEVICES_WITH_PK_FILTER = "SELECT COUNT(*) as total FROM public.%s where %s = #{tenantId} and %s = #{deviceId}";


    default Future<Integer> searchForDevice(SqlConnection sqlConnection, String deviceId, String tenantId, DatabaseConfig databaseConfig) {
        var sql = String.format(SQL_COUNT_DEVICES_WITH_PK_FILTER,
                databaseConfig.getDeviceRegistrationTableName(),
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationDeviceIdColumn());


        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");
        return SqlTemplate
                .forQuery(sqlConnection, sql)
                .mapTo(ROW_MAPPER)
                .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.next();
                });

    }
}
