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
 * Device constant values.
 */
public final class ApiCommonConstants {

    /**
     * Path parameter name for tenantId.
     */
    public static final String TENANT_PATH_PARAMS = "tenantid";
    /**
     * Path parameter name for deviceId.
     */
    public static final String DEVICE_PATH_PARAMS = "deviceid";

    private ApiCommonConstants() {
        // avoid instantiation
    }
}
