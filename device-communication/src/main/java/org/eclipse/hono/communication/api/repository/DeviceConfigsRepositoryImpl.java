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

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.graalvm.collections.Pair;

import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;

/**
 * Repository class for making CRUD operations for device config entities.
 */
@ApplicationScoped
public class DeviceConfigsRepositoryImpl implements DeviceConfigsRepository {
    private final String SQL_INSERT = "INSERT INTO device_configs (version, tenant_id, device_id, cloud_update_time, device_ack_time, binary_data) " +
            "VALUES (#{version}, #{tenantId}, #{deviceId}, #{cloudUpdateTime}, #{deviceAckTime}, #{binaryData}) RETURNING version";
    private final String SQL_LIST = "SELECT version, cloud_update_time, device_ack_time, binary_data " +
            "FROM device_configs WHERE device_id = #{deviceId} and tenant_id =  #{tenantId} ORDER BY version DESC LIMIT #{limit}";
    private final String SQL_DELETE_MIN_VERSION = "DELETE FROM device_configs WHERE device_id = #{deviceId} and tenant_id =  #{tenantId} " +
            "and version = (SELECT MIN(version) from  device_configs WHERE device_id = #{deviceId} and tenant_id =  #{tenantId})  RETURNING version";
    private final String SQL_FIND_TOTAL_AND_MAX_VERSION = "SELECT COALESCE(COUNT(*), 0) as total, COALESCE(MAX(version), 0) as max_version from device_configs " +
            "WHERE device_id = #{deviceId} and tenant_id =  #{tenantId}";

    private final int MAX_LIMIT = 10;
    private final Logger log = LoggerFactory.getLogger(DeviceConfigsRepositoryImpl.class);
    private String SQL_COUNT_DEVICES_WITH_PK_FILTER = "SELECT COUNT(*) as total FROM public.%s where %s = #{tenantId} and %s = #{deviceId}";


    /**
     * Creates a new  DeviceConfigsRepositoryImpl.
     *
     * @param databaseConfig The database configs
     */
    public DeviceConfigsRepositoryImpl(final DatabaseConfig databaseConfig) {

        SQL_COUNT_DEVICES_WITH_PK_FILTER = String.format(SQL_COUNT_DEVICES_WITH_PK_FILTER,
                databaseConfig.getDeviceRegistrationTableName(),
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationDeviceIdColumn());
    }


    private Future<Integer> searchForDevice(final SqlConnection sqlConnection, final String deviceId, final String tenantId) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");
        return SqlTemplate
                .forQuery(sqlConnection, SQL_COUNT_DEVICES_WITH_PK_FILTER)
                .mapTo(ROW_MAPPER)
                .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.next();
                });

    }

    private Future<Pair<Integer, Integer>> findMaxVersionAndTotalEntries(final SqlConnection sqlConnection, final String deviceId, final String tenantId) {
        final RowMapper<Pair<Integer, Integer>> ROW_MAPPER = row ->
                Pair.create(row.getInteger("total"), row.getInteger("max_version"));
        return SqlTemplate
                .forQuery(sqlConnection, SQL_FIND_TOTAL_AND_MAX_VERSION)
                .mapTo(ROW_MAPPER)
                .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                    final RowIterator<Pair<Integer, Integer>> iterator = rowSet.iterator();
                    return iterator.next();
                });

    }

    @Override
    public Future<List<DeviceConfig>> listAll(final SqlConnection sqlConnection, final String deviceId, final String tenantId, final int limit) {
        final int queryLimit = limit == 0 ? MAX_LIMIT : limit;
        return searchForDevice(sqlConnection, deviceId, tenantId)
                .compose(
                        counter -> {
                            if (counter < 1) {
                                throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                        deviceId,
                                        tenantId));
                            }
                            return SqlTemplate
                                    .forQuery(sqlConnection, SQL_LIST)
                                    .mapTo(DeviceConfig.class)
                                    .execute(Map.of("deviceId", deviceId, "tenantId", tenantId, "limit", queryLimit))
                                    .map(rowSet -> {
                                        final List<DeviceConfig> configs = new ArrayList<>();
                                        rowSet.forEach(configs::add);
                                        return configs;
                                    })
                                    .onSuccess(success -> log.info(
                                            String.format("Listing all configs for device %s and tenant %s",
                                                    deviceId, tenantId)))
                                    .onFailure(throwable -> log.error("Error: {}", throwable));
                        });
    }


    /**
     * Inserts a new entity in to the db.
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    private Future<DeviceConfigEntity> insert(final SqlConnection sqlConnection, final DeviceConfigEntity entity) {
        return SqlTemplate
                .forUpdate(sqlConnection, SQL_INSERT)
                .mapFrom(DeviceConfigEntity.class)
                .mapTo(DeviceConfigEntity.class)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<DeviceConfigEntity> iterator = rowSet.iterator();
                    if (iterator.hasNext()) {
                        entity.setVersion(iterator.next().getVersion());
                        return entity;
                    } else {
                        throw new IllegalStateException(String.format("Can't create device config: %s", entity));
                    }
                })
                .onSuccess(success -> log.info(String.format("Device config created successfully: %s", success.toString())))
                .onFailure(throwable -> log.error(throwable.getMessage()));

    }

    /**
     * Delete the smallest config version.
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The device config for searching and deleting the smallest version
     * @return A Future of the deleted version
     */

    private Future<Integer> deleteMinVersion(final SqlConnection sqlConnection, final DeviceConfigEntity entity) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("version");
        return SqlTemplate
                .forQuery(sqlConnection, SQL_DELETE_MIN_VERSION)
                .mapFrom(DeviceConfigEntity.class)
                .mapTo(ROW_MAPPER)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.next();
                })
                .onSuccess(deletedVersion -> log.info(String.format("Device config version %s was deleted", deletedVersion)));
    }


    @Override
    public Future<DeviceConfigEntity> createNew(final SqlConnection sqlConnection, final DeviceConfigEntity entity) {
        return searchForDevice(sqlConnection, entity.getDeviceId(), entity.getTenantId())
                .compose(
                        counter -> {
                            if (counter < 1) {
                                throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                        entity.getDeviceId(),
                                        entity.getTenantId()));
                            }
                            return findMaxVersionAndTotalEntries(sqlConnection, entity.getDeviceId(), entity.getTenantId())
                                    .compose(
                                            values -> {
                                                final int total = values.getLeft();
                                                final int maxVersion = values.getRight();

                                                entity.setVersion(maxVersion + 1);

                                                if (total > MAX_LIMIT - 1) {
                                                    return deleteMinVersion(sqlConnection, entity).compose(
                                                            ok -> insert(sqlConnection, entity)

                                                    );
                                                }
                                                return insert(sqlConnection, entity);
                                            }
                                    );
                        }).onFailure(error -> log.error(error.getMessage()));
    }
}
