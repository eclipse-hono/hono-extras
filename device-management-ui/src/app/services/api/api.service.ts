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

import {Injectable} from '@angular/core';
import {HttpHeaders} from "@angular/common/http";
import {GoogleService} from "../google/google.service";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private googleService: GoogleService) {
  }

  public getHttpsRequestOptions() {
    const idToken = this.googleService.getIdToken();
    const header = new HttpHeaders()
      .set('Accept', 'application/json')
      .set('Authorization', `Bearer ${idToken}`)
      .set('Content-Type', 'application/json')

    return {
      headers: header,
      withCredentials: true,
      observe: 'body' as 'response'
    }
  }

}
