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

import {ApiService} from './api.service';
import {GoogleService} from "../google/google.service";

describe('ApiService', () => {
  let service: ApiService;
  let googleServiceSpy: {
    getIdToken: jasmine.Spy;
  }

  beforeEach(() => {
    googleServiceSpy = jasmine.createSpyObj('GoogleService', ['getIdToken']);
    TestBed.configureTestingModule({
      providers: [
        {provide: GoogleService, useValue: googleServiceSpy}
      ]
    });
    service = TestBed.inject(ApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
