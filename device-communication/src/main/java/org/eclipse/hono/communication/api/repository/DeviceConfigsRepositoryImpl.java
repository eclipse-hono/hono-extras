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
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.eclipse.hono.communication.api.entity.DeviceConfig;
import org.eclipse.hono.communication.api.entity.DeviceConfigEntity;
import org.eclipse.hono.communication.core.app.DatabaseConfig;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository class for making CRUD operations for device config entities
 */
@ApplicationScoped
public class DeviceConfigsRepositoryImpl implements DeviceConfigsRepository {
    private final static String SQL_INSERT = "INSERT INTO \"deviceConfig\" (version, \"tenantId\", \"deviceId\", \"cloudUpdateTime\", \"deviceAckTime\", \"binaryData\") " +
            "VALUES (#{version}, #{tenantId}, #{deviceId}, #{cloudUpdateTime}, #{deviceAckTime}, #{binaryData}) RETURNING version";
    private final static String SQL_UPDATE = "UPDATE \"deviceConfig\" SET \"cloudUpdateTime\" = #{cloudUpdateTime}, \"deviceAckTime\" = #{deviceAckTime}, " +
            "\"binaryData\" = #{binaryData} WHERE version = #{version} and \"tenantId\" = #{tenantId} and \"deviceId\" = #{deviceId}";
    private final static String SQL_LIST = "SELECT version, \"cloudUpdateTime\", COALESCE(\"deviceAckTime\",'') AS  \"deviceAckTime\", \"binaryData\" " +
            "FROM \"deviceConfig\" WHERE \"deviceId\" = #{deviceId} and \"tenantId\" =  #{tenantId} ORDER BY version DESC LIMIT #{limit}";
    private final static String SQL_FIND_MAX_VERSION = "SELECT COALESCE(max(version), 0) AS version from \"deviceConfig\" " +
            "WHERE \"deviceId\" = #{deviceId} and \"tenantId\" =  #{tenantId}";
    private final Logger log = LoggerFactory.getLogger(DeviceConfigsRepositoryImpl.class);
    private final DatabaseConfig databaseConfig;
    private String SQL_COUNT_DEVICES_WITH_PK_FILTER = "SELECT COUNT(*) as total FROM %s where %s = #{tenantId} and %s = #{deviceId}";

    public DeviceConfigsRepositoryImpl(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;

        SQL_COUNT_DEVICES_WITH_PK_FILTER = String.format(SQL_COUNT_DEVICES_WITH_PK_FILTER,
                databaseConfig.getDeviceRegistrationTableName(),
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationDeviceIdColumn());
    }


    private Future<Integer> countDevices(SqlConnection sqlConnection, String deviceId, String tenantId) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");
        return SqlTemplate
                .forQuery(sqlConnection, SQL_COUNT_DEVICES_WITH_PK_FILTER)
                .mapTo(ROW_MAPPER)
                .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.next();
                });

    }

    /**
     * Lists all config versions for a specific device. Result is order by version desc
     *
     * @param sqlConnection The sql connection instance
     * @param deviceId      The device id
     * @param tenantId      The tenant id
     * @param limit         The number of config to show
     * @return A Future with a List of DeviceConfigs
     */
    public Future<List<DeviceConfig>> listAll(SqlConnection sqlConnection, String deviceId, String tenantId, int limit) {
        return SqlTemplate
                .forQuery(sqlConnection, SQL_LIST)
                .mapTo(DeviceConfig.class)
                .execute(Map.of("limit", limit, "deviceId", deviceId, "tenantId", tenantId))
                .map(rowSet -> {
                    final List<DeviceConfig> configs = new ArrayList<>();
                    rowSet.forEach(configs::add);
                    return configs;
                })
                .onSuccess(success -> log.info(
                        String.format("Listing all configs for device %s and tenant %s",
                                deviceId, tenantId)))
                .onFailure(throwable -> log.error("Error: {}", throwable));
    }

    /**
     * Updates an entity for a given version
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    private Future<DeviceConfigEntity> update(SqlConnection sqlConnection, DeviceConfigEntity entity) {

        return SqlTemplate
                .forQuery(sqlConnection, SQL_UPDATE)
                .mapFrom(DeviceConfigEntity.class)
                .execute(entity)
                .map(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        return entity;
                    } else {
                        throw new IllegalStateException(String.format("Device config version doesn't exist: %s", entity));
                    }
                })
                .onSuccess(success -> log.info(String.format("Device config updated successfully: %s", success.toString())))
                .onFailure(throwable -> log.error("Error: {}", throwable));
    }

    /**
     * Increases the version number and creates a new entity.
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    private Future<DeviceConfigEntity> create(SqlConnection sqlConnection, DeviceConfigEntity entity) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("version");

        return SqlTemplate
                .forQuery(sqlConnection, SQL_FIND_MAX_VERSION)
                .mapFrom(DeviceConfigEntity.class)
                .mapTo(ROW_MAPPER)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.hasNext() ? iterator.next() + 1 : 1;
                }).compose(maxResults -> {
                            entity.setVersion(maxResults);
                            return insertEntity(sqlConnection, entity);
                        }
                )
                .onSuccess(success -> log.info(String.format("Device configs created successfully: %s", success.toString())))
                .onFailure(throwable -> log.error("Error: {}", throwable));
    }

    /**
     * Inserts an entity to Database
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    private Future<DeviceConfigEntity> insertEntity(SqlConnection sqlConnection, DeviceConfigEntity entity) {
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
                });
    }


    /**
     * Creates a new config if version is 0 else updates the current config
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    public Future<DeviceConfigEntity> createOrUpdate(SqlConnection sqlConnection, DeviceConfigEntity entity) {
        return countDevices(sqlConnection, entity.getDeviceId(), entity.getTenantId())
                .compose(
                        counter -> {
                            if (counter < 1) {
                                throw new IllegalStateException(String.format("Device with id %s and tenant id %s doesn't exist",
                                        entity.getDeviceId(),
                                        entity.getTenantId()));
                            } else {

                                if (entity.getVersion() == 0) {
                                    return create(sqlConnection, entity);
                                } else if (entity.getVersion() > 0) {
                                    return update(sqlConnection, entity);
                                } else {
                                    throw new IllegalStateException("Config version must be >= 0");
                                }
                            }
                        });
    }
}