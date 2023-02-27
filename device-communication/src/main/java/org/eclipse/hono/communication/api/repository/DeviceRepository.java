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

import java.util.List;

import io.vertx.core.Future;


/**
 * Device repository interface.
 */
public interface DeviceRepository {

    /**
     * Check if device exist.
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @return Future of integer
     */
    Future<Integer> searchForDevice(String deviceId, String tenantId);

    /**
     * Lists all unique tenants.
     *
     * @return Future of list with all tenants.
     */
    Future<List<String>> listDistinctTenants();

}
