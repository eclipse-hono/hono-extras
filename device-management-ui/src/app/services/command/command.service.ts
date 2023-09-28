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
import {Command} from "../../models/command";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})
export class CommandService {

  private commandUrlSuffix: string = 'v1/commands/:tenantId/:deviceId'

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public sendCommand(deviceId: string, tenantId: string, command: Command): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.commandUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.post(url, command, header);
  }
}
