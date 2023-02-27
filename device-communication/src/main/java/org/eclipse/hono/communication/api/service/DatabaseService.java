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

package org.eclipse.hono.communication.api.service;

import io.vertx.pgclient.PgPool;

/**
 * Database service interface.
 */
public interface DatabaseService {
    /**
     * Gets the database client instance.
     *
     * @return The database client
     */
    PgPool getDbClient();

    /**
     * Closes the database connection.
     */
    void close();
}
