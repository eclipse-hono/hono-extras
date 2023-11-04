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

import {TestBed} from '@angular/core/testing';

import {CredentialsService} from './credentials.service';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../api/api.service";
import {of} from "rxjs";
import {Credentials} from "../../models/credentials/credentials";

describe('CredentialsService', () => {
  let service: CredentialsService;
  let httpClientSpy: {
    put: jasmine.Spy;
    get: jasmine.Spy;
  };
  let apiServiceSpy: {
    getHttpsRequestOptions: jasmine.Spy;
  };

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['put', 'get']);
    apiServiceSpy = jasmine.createSpyObj('ApiService', ['getHttpsRequestOptions']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue: httpClientSpy},
        {provide: ApiService, useValue: apiServiceSpy}
      ]
    });
    service = TestBed.inject(CredentialsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return empty body when save is called', () => {
    const request: Credentials[] = [new Credentials()];
    const result = {};
    httpClientSpy.put.and.returnValue(of(result));


    service.save('test-device', 'test-tenant', request).subscribe((success) => {
      expect(success).toEqual({});
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.put).toHaveBeenCalled();
  });

  it('should return credentials list when list is called', () => {
    const result: Credentials[] = [new Credentials(), new Credentials()];
    httpClientSpy.get.and.returnValue(of(result));

    service.list('test-device', 'test-tenant').subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.length).toEqual(2);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.get).toHaveBeenCalled();
  });
});
