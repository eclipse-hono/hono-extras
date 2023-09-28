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

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectDevicesComponent } from './select-devices.component';
import {HttpClientModule} from "@angular/common/http";
import {OAuthModule} from "angular-oauth2-oidc";
import {Device} from "../../../models/device";

describe('SelectDevicesComponent', () => {
  let component: SelectDevicesComponent;
  let fixture: ComponentFixture<SelectDevicesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectDevicesComponent ],
      imports: [HttpClientModule, OAuthModule.forRoot()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectDevicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return true when devices list is empty', () => {
    component.devices = [];

    expect(component.deviceListIsEmpty()).toBeTrue();
  });

  it('should return false when devices list is not empty', () => {
    component.devices = [new Device()];

    expect(component.deviceListIsEmpty()).toBeFalse();
  });

  it('should return true when device.via list is empty', () => {
    component.device.via = [];

    expect(component.devicesSelected()).toBeTrue();
  });

  it('should return false when device.via list is not empty', () => {
    component.device.via = ['gatewayId'];

    expect(component.devicesSelected()).toBeFalse();
  });

  it('should return true when selectedDevices list is empty', () => {
    component.selectedDevices = [];

    expect(component.selectedDevicesListEmpty()).toBeTrue();
  });

  it('should return false when selectedDevices list is not empty', () => {
    component.selectedDevices = [new Device()];

    expect(component.selectedDevicesListEmpty()).toBeFalse();
  });

  it('should add the selected device to selectedDevices array and emit the event', () => {
    const device = new Device();
    device.id = 'test-device-id';

    spyOn(component.selectedDevicesChanged, 'emit');

    component.selectDevice(device);

    expect(device.checked).toBeTrue();
    expect(component.selectedDevices).toContain(device);
    expect(component.selectedDevicesChanged.emit).toHaveBeenCalledWith(component.selectedDevices);
  });

  it('should not add the same selected device to selectedDevices array multiple times', () => {
    const device = new Device();
    device.id = 'test-device-id';

    spyOn(component.selectedDevicesChanged, 'emit');

    component.selectDevice(device);
    component.selectDevice(device);
    component.selectDevice(device);

    expect(component.selectedDevices).toContain(device);
    expect(component.selectedDevices.length).toBe(1);
    expect(component.selectedDevicesChanged.emit).toHaveBeenCalledTimes(1);
  });

  it('should remove the selected device from selectedDevice array and emit the event', () => {
    const device = new Device();
    device.id = 'test-device-id';
    component.selectedDevices= [device];

    spyOn(component.selectedDevicesChanged, 'emit');

    component.unselectDevice(device);

    expect(device.checked).toBeFalse();
    expect(component.selectedDevices).not.toContain(device);
    expect(component.selectedDevicesChanged.emit).toHaveBeenCalledWith(component.selectedDevices);
  })

  it('should not emit the event if the device is not found in selectedDevices array', () => {
    const device = new Device();
    device.id = 'test-device-id';

    spyOn(component.selectedDevicesChanged, 'emit');

    component.unselectDevice(device);

    expect(component.selectedDevicesChanged.emit).not.toHaveBeenCalled();
  });

  it('should update the pageOffset and emit the event when changing the page', () => {
    component['pageOffset'] = 50;
    const pageSize = 10;

    spyOn(component.pageOffsetChanged, 'emit');

    component.pageSize = pageSize;
    component.changePage(6);

    expect(component['pageOffset']).toBe(50);
    expect(component.pageOffsetChanged.emit).toHaveBeenCalledWith(50);
  });

});
