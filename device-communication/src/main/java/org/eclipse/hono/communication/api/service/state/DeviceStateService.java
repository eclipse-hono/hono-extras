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

package org.eclipse.hono.communication.api.service.state;

import org.eclipse.hono.communication.api.data.ListDeviceStatesResponse;

import io.vertx.core.Future;

/**
 * Device state interface.
 */
public interface DeviceStateService {

    /**
     * Lists all the states for a specific device.
     *
     * @param deviceId Device Id
     * @param tenantId Tenant Id
     * @param limit Limit between 1 and 10
     * @return Future of ListDeviceStatesResponse
     */
    Future<ListDeviceStatesResponse> listAll(String deviceId, String tenantId, int limit);
}
