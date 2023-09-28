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

import {StatesService} from './states.service';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../api/api.service";
import {of} from "rxjs";
import {State} from "../../models/state";

describe('StatesService', () => {
  let service: StatesService;
  let httpClientSpy: {
    get: jasmine.Spy;
  };
  let apiServiceSpy: {
    getHttpsRequestOptions: jasmine.Spy;
  };

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
    apiServiceSpy = jasmine.createSpyObj('ApiService', ['getHttpsRequestOptions']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue: httpClientSpy},
        {provide: ApiService, useValue: apiServiceSpy}
      ]
    });
    service = TestBed.inject(StatesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return list of two states when list is called', () => {
    const result: State[] = [new State(), new State()];
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
