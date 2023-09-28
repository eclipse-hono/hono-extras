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
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Credentials} from "../../models/credentials/credentials";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})
export class CredentialsService {

  private credentialsUrlSuffix: string = '/v1/credentials/:tenantId/:deviceId';

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public save(deviceId: string, tenantId: string, credentials: Credentials[]): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.credentialsUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.put(url, credentials, header);
  }

  public list(deviceId: string, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.credentialsUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.get(url, header);
  }

}
