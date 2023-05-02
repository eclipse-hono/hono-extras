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

package org.eclipse.hono.communication.api.config;

/**
 * Device configs constant values.
 */
public final class DeviceConfigsConstants {

    /**
     * OpenApi GET device configs operation id.
     */
    public static final String LIST_CONFIG_VERSIONS_OP_ID = "listConfigVersions";
    /**
     * Path parameter name for tenantId.
     */
    public static final String TENANT_PATH_PARAMS = "tenantid";
    /**
     * Path parameter name for deviceId.
     */
    public static final String DEVICE_PATH_PARAMS = "deviceid";
    /**
     * Path parameter name for number of versions.
     */
    public static final String NUM_VERSION_QUERY_PARAMS = "numVersions";

    /**
     * Sql migrations script path.
     */
    public static final String CREATE_SQL_SCRIPT_PATH = "db/create_device_config_table.sql";
    /**
     * OpenApi POST device configs operation id.
     */
    public static final String POST_MODIFY_DEVICE_CONFIG_OP_ID = "modifyCloudToDeviceConfig";

    private DeviceConfigsConstants() {
        // avoid instantiation
    }
}
