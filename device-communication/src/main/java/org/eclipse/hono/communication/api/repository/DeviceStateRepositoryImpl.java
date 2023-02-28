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
import java.util.List;
import java.util.Map;

import org.eclipse.hono.communication.api.data.DeviceState;
import org.eclipse.hono.communication.api.data.DeviceStateEntity;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.SqlTemplate;

/**
 * Repository class for making CRUD operations for device state entities.
 */
public class DeviceStateRepositoryImpl implements DeviceStateRepository {

    private static final Logger log = LoggerFactory.getLogger(DeviceStateRepositoryImpl.class);
    private final String SQL_INSERT = "INSERT INTO device_status (version, tenant_id, device_id, cloud_update_time, binary_data) "
            +
            "VALUES (#{version}, #{tenantId}, #{deviceId}, #{cloudUpdateTime}, #{binaryData}) RETURNING version";
    private final String SQL_LIST = "SELECT version, cloud_update_time, binary_data " +
            "FROM device_status WHERE device_id = #{deviceId} and tenant_id =  #{tenantId} ORDER BY version DESC LIMIT #{limit}";
    private final int MAX_LIMIT = 10;
    private final DatabaseConfig databaseConfig;
    private final DatabaseService db;
    private final DeviceRepository deviceRepository;

    /**
     * Creates a new DeviceStateRepositoryImpl.
     *
     * @param db               The database connection
     * @param databaseConfig   The database configs
     * @param deviceRepository The device repository interface
     */
    public DeviceStateRepositoryImpl(final DatabaseService db,
            final DatabaseConfig databaseConfig,
            final DeviceRepository deviceRepository) {

        this.databaseConfig = databaseConfig;
        this.db = db;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Future<List<DeviceState>> listAll(final String deviceId, final String tenantId, final int limit) {
        final int queryLimit = limit == 0 ? MAX_LIMIT : limit;
        return db.getDbClient().withConnection(
                sqlConnection -> deviceRepository.searchForDevice(deviceId, tenantId)
                        .compose(
                                counter -> {
                                    if (counter < 1) {
                                        throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                                deviceId,
                                                tenantId));
                                    }
                                    return SqlTemplate
                                            .forQuery(sqlConnection, SQL_LIST)
                                            .mapTo(DeviceState.class)
                                            .execute(Map.of("deviceId", deviceId, "tenantId", tenantId, "limit", queryLimit))
                                            .map(rowSet -> {
                                                final List<DeviceState> states = new ArrayList<>();
                                                rowSet.forEach(states::add);
                                                return states;
                                            })
                                            .onSuccess(success -> log.info(
                                                    String.format("Listing all states for device %s and tenant %s",
                                                            deviceId, tenantId)))
                                            .onFailure(throwable -> log.error("Error: {}", throwable.getMessage()));
                                }));
    }

    @Override
    public Future<List<String>> listTenants() {
        return deviceRepository.listDistinctTenants();
    }

    @Override
    public Future<DeviceStateEntity> createNew(final DeviceStateEntity entity) {
        return db.getDbClient().withTransaction(
                sqlConnection -> deviceRepository.searchForDevice(entity.getDeviceId(), entity.getTenantId())
                        .compose(
                                counter -> {
                                    if (counter < 1) {
                                        throw new DeviceNotFoundException(
                                                String.format("Device with id %s and tenant id %s doesn't exist",
                                                        entity.getDeviceId(),
                                                        entity.getTenantId()));
                                    }
                                    return insert(sqlConnection, entity);
                                })
                        .onFailure(error -> log.error(error.getMessage())));
    }

    /**
     * Inserts a new entity in to the db.
     *
     * @param sqlConnection The sql connection instance.
     * @param entity The instance to insert.
     * @return A Future of the created DeviceStateEntity.
     */
    private Future<DeviceStateEntity> insert(final SqlConnection sqlConnection, final DeviceStateEntity entity) {
        return SqlTemplate
                .forUpdate(sqlConnection, SQL_INSERT)
                .mapFrom(DeviceStateEntity.class)
                .mapTo(DeviceStateEntity.class)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<DeviceStateEntity> iterator = rowSet.iterator();
                    if (iterator.hasNext()) {
                        return entity;
                    } else {
                        throw new IllegalStateException(String.format("Can't create device state: %s", entity));
                    }
                })
                .onSuccess(success -> log.info("Device state created successfully: {}", success.toString()))
                .onFailure(throwable -> log.error(throwable.getMessage()));

    }
}
