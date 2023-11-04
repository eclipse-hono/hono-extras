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

export class Secret {
  id?: string;

  enabled?: boolean;

  'not-before'?: string;

  'not-after'?: string;

  comment?: string;

  'hash-function'?: string;

  'pwd-hash'?: string;

  salt?: string;

  'pwd-plain'?: string;

  algorithm?: string = '';

  key?: string = '';

  cert?: string = '';
}
