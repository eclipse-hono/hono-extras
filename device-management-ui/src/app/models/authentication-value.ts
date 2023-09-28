/*
 * *******************************************************************************
 *  * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  *
 *  * See the NOTICE file(s) distributed with this work for additional
 *  * information regarding copyright ownership.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 */

import {CredentialTypes} from "./credentials/credentials";

export class AuthenticationValue {
  type?: CredentialTypes | string;

  'auth-id'?: string;

  'not-after'?: string;

  'not-before'?: string;

  algorithm?: string;

  key?: string;

  id?: string;

}
