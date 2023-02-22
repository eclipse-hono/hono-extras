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
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.core.app.DatabaseConfig;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Device registrations repository
 */
@ApplicationScoped
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DatabaseConfig databaseConfig;
    private final DatabaseService db;
    String SQL_COUNT_DEVICES_WITH_PK_FILTER = "SELECT COUNT(*) as total FROM public.%s where %s = #{tenantId} and %s = #{deviceId}";
    String SQL_LIST_DISTINCT_TENANTS = "SELECT DISTINCT %s FROM %s";

    public DeviceRepositoryImpl(DatabaseConfig databaseConfig, DatabaseService databaseService) {

        this.databaseConfig = databaseConfig;
        this.db = databaseService;

        SQL_COUNT_DEVICES_WITH_PK_FILTER = String.format(SQL_COUNT_DEVICES_WITH_PK_FILTER,
                databaseConfig.getDeviceRegistrationTableName(),
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationDeviceIdColumn());
    }


    /**
     * Check if device exist
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @return Future of Integer
     */
    @Override
    public Future<Integer> searchForDevice(String deviceId, String tenantId) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");
        return db.getDbClient().withConnection(
                sqlConnection -> SqlTemplate
                        .forQuery(sqlConnection, SQL_COUNT_DEVICES_WITH_PK_FILTER)
                        .mapTo(ROW_MAPPER)
                        .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                            final RowIterator<Integer> iterator = rowSet.iterator();
                            return iterator.next();
                        }));
    }


    /**
     * List all tenants  distinct from device_registration table
     *
     * @return Future of List of tenants
     */
    @Override
    public Future<List<String>> listDistinctTenants() {

        var sqlCommand = SQL_LIST_DISTINCT_TENANTS.formatted(
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationTableName());

        return db.getDbClient().withConnection(
                sqlConnection -> SqlTemplate
                        .forQuery(sqlConnection, sqlCommand)
                        .execute(Collections.emptyMap())
                        .map(rowSet -> {
                            final List<String> tenants = new ArrayList<>();
                            rowSet.forEach(tenant -> tenants.add(tenant.getString(databaseConfig.getDeviceRegistrationTenantIdColumn())));
                            return tenants;
                        }));

    }
}
