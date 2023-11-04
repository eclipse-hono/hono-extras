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

import {Secret} from "./secret";

export class Credentials {
  type?: CredentialTypes | string;
  'auth-id'?: string;
  enabled?: boolean;
  ext?: any;
  secrets: Secret[] = [];
}

export enum CredentialTypes {
  HASHED_PASSWORD = 'hashed-password',
  RPK = 'rpk'
}
