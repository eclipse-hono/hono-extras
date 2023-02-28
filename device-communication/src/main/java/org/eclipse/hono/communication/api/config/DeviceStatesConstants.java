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
 * Device states constant values.
 */
public final class DeviceStatesConstants {

    /**
     * OpenApi GET device states operation id.
     */
    public static final String LIST_STATES_OP_ID = "listStates";
    /**
     * Path parameter name for number of states.
     */
    public static final String NUM_STATES_QUERY_PARAMS = "numStates";

    private DeviceStatesConstants() {
        // avoid instantiation
    }
}
