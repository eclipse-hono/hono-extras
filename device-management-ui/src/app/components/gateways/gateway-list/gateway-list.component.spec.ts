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

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GatewayListComponent} from './gateway-list.component';
import {HttpClientModule} from "@angular/common/http";
import {OAuthModule} from "angular-oauth2-oidc";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {NgbModule, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {Device} from "../../../models/device";
import {Tenant} from "../../../models/tenant";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";

describe('GatewayListComponent', () => {
  let component: GatewayListComponent;
  let fixture: ComponentFixture<GatewayListComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GatewayListComponent],
      imports: [HttpClientModule, OAuthModule.forRoot(), FontAwesomeTestingModule, NgbPagination, NgbModule, RouterTestingModule],
    })
      .compileComponents();

    fixture = TestBed.createComponent(GatewayListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return creationTime in gateway list', () => {
    const status = {created: '12345'};

    const result = component['getCreationTime'](status);
    expect(result).toEqual('12345');
  });

  it('should return "-" in gateway list', () => {
    const status = {name: '12345'};

    const result = component['getCreationTime'](status);
    expect(result).toEqual('-');
  });

  it('should navigate to gateway detail on selectGateway', () => {
    const device = new Device();
    device.id = 'gateway-id';
    const tenant = new Tenant();
    tenant.id = 'tenant-id';
    const isGateway = true;

    component.tenant = tenant;

    component['selectGateway'](device);

    expect(router.navigate).toHaveBeenCalledWith(
      ['device-detail', 'gateway-id'],
      {state: {tenant, device, isGateway}}
    );
  });
});
