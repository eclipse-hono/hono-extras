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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.core.app.DatabaseConfig;

import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;


/**
 * Device registrations repository.
 */
@ApplicationScoped
public class DeviceRepositoryImpl implements DeviceRepository {

    private final DatabaseConfig databaseConfig;
    private final DatabaseService db;
    private final String SQL_COUNT_DEVICES_WITH_PK_FILTER;
    private final String SQL_LIST_DISTINCT_TENANTS = "SELECT DISTINCT %s FROM %s";

    /**
     * Creates a new DeviceRepositoryImpl.
     *
     * @param databaseConfig  The database configs
     * @param databaseService The database service
     */
    public DeviceRepositoryImpl(final DatabaseConfig databaseConfig, final DatabaseService databaseService) {

        this.databaseConfig = databaseConfig;
        this.db = databaseService;

        SQL_COUNT_DEVICES_WITH_PK_FILTER = String.format("SELECT COUNT(*) as total FROM public.%s where %s = #{tenantId} and %s = #{deviceId}",
                databaseConfig.getDeviceRegistrationTableName(),
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationDeviceIdColumn());
    }


    @Override
    public Future<Integer> searchForDevice(final String deviceId, final String tenantId) {
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


    @Override
    public Future<List<String>> listDistinctTenants() {

        final var sqlCommand = SQL_LIST_DISTINCT_TENANTS.formatted(
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
