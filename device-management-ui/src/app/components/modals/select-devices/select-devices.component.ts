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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Device} from "../../../models/device";
@Component({
  selector: 'app-selected-devices',
  templateUrl: './select-devices.component.html',
  styleUrls: ['./select-devices.component.scss']
})
export class SelectDevicesComponent {

  @Input() public device: Device = new Device();
  @Input() public sendViaGateway: boolean = false;
  @Input() public tenantId: string = '';
  @Input() public devices: Device[] = [];
  @Input() public deviceListCount: number = 0;
  @Input() public createDevice: boolean = false;
  @Input() public pageSize: number = 50;
  @Input() public bindDevices: boolean = false;
  @Input() public selectDevicesLabel: string = '';

  @Output() public pageOffsetChanged: EventEmitter<number> = new EventEmitter<number>();
  @Output() public selectedDevicesChanged: EventEmitter<Device[]> = new EventEmitter<Device[]>();

  public selectedDevices: Device[] = [];
  public searchTerm!: string;

  private pageOffset: number = 0;

  public selectDevice(selectedDevice: Device) {
    selectedDevice.checked = true;
    if (!this.selectedDevices.includes(selectedDevice)) {
      this.selectedDevices.push(selectedDevice);
      this.selectedDevicesChanged.emit(this.selectedDevices);
    }
  }

  public unselectDevice(selectedDevice: Device) {
    selectedDevice.checked = false;
    const index = this.selectedDevices.indexOf(selectedDevice);
    if (index != undefined && index >= 0) {
      this.selectedDevices.splice(index, 1);
      this.selectedDevicesChanged.emit(this.selectedDevices);
    }
  }

  public deviceListIsEmpty(): boolean {
    return !this.devices || this.devices.length === 0;
  }

  public devicesSelected(): boolean {
    return !this.device.via || this.device.via.length === 0;
  }

  public selectedDevicesListEmpty(): boolean {
    return !this.selectedDevices || this.selectedDevices.length === 0;
  }

  public changePage($event: number) {
    this.pageOffset = ($event -1) * this.pageSize;
    this.pageOffsetChanged.emit(this.pageOffset);
  }

}
