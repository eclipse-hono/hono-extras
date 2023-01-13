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
 * Device configs constant values
 */
public class DeviceConfigsConstants {

    // Open api operationIds
    public final static String POST_MODIFY_DEVICE_CONFIG_OP_ID = "modifyCloudToDeviceConfig";
    public final static String LIST_CONFIG_VERSIONS_OP_ID = "listConfigVersions";
    public final static String TENANT_PATH_PARAMETER = "tenantid";
    public final static String DEVICE_PATH_PARAMETER = "deviceid";
    public final static String NUM_VERSION_QUERY_PARAMS = "numVersions";
}
