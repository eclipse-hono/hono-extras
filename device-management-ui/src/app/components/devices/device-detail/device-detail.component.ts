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

import {Component} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeleteComponent} from '../../modals/delete/delete.component';
import {Device} from "../../../models/device";
import { Router} from "@angular/router";
import {Tenant} from "../../../models/tenant";
import {UpdateConfigModalComponent} from '../../modals/update-config-modal/update-config-modal.component';
import {SendCommandComponent} from '../../modals/send-command/send-command.component';
import {DeviceService} from "../../../services/device/device.service";
import {ConfigService} from "../../../services/config/config.service";
import {Config} from "../../../models/config";
import {CredentialsModalComponent} from '../../modals/credentials-modal/credentials-modal.component';
import {Credentials} from 'src/app/models/credentials/credentials';
import {CredentialsService} from "../../../services/credentials/credentials.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {DatePipe, Location} from "@angular/common";
import {CreateAndBindModalComponent} from "../../modals/create-and-bind-modal/create-and-bind-modal.component";

@Component({
  selector: 'app-device-detail',
  templateUrl: './device-detail.component.html',
  styleUrls: ['./device-detail.component.scss']
})
export class DeviceDetailComponent {

  public isGateway: boolean = false;
  public device: Device = new Device();
  public gateway: Device = new Device();
  public tenant: Tenant = new Tenant();
  public devices: Device[] = [];
  public boundDevicesList: Device[] = [];
  public configs: Config[] = [];
  public credentials: Credentials[] = [];
  public deviceListCount: number = 0;
  public boundDeviceListCount: number = 0;
  public pageSize: number = 50;
  public isBoundDevice: boolean = false;

  private pageOffset: number = 0;

  constructor(private modalService: NgbModal,
              private router: Router,
              private location: Location,
              private deviceService: DeviceService,
              private configService: ConfigService,
              private credentialsService: CredentialsService,
              private notificationService: NotificationService,
              private datePipe: DatePipe) {
    const navigation = this.router.getCurrentNavigation();
    let accessedViaUrl: boolean = false;
    if (navigation) {
      const state = navigation.extras.state
      if (state && state['tenant']) {
        this.tenant = state['tenant'];
      }
      if (state && state['device']) {
        this.device = state['device'];
        this.setDevices();
        this.setIsGatewayFlag();
      } else {
        accessedViaUrl = true;
        const urlSegments = window.location.pathname.substring(1).split("/");
        const tenantId = String(urlSegments[1]);
        const deviceId = String(urlSegments[2]);
        this.deviceService.getByExactId(tenantId, deviceId).subscribe({
          next: (result) => {
            result.id = deviceId;
            this.tenant = new Tenant();
            this.tenant.id = tenantId;
            this.device = result;
            this.setDevices();
            this.setIsGatewayFlag();
            this.setUpDeviceDetail();
          }
        })
      }

    }
    if (!accessedViaUrl) {
      this.setUpDeviceDetail();
    }
  }

  private setUpDeviceDetail() {
    if(this.isGateway) {
      this.gateway = this.device;
    }
    this.getConfigs();
    this.getCredentials();
    this.checkIsBoundDevice();
  }
  private setIsGatewayFlag() {
    const storedIsGateway = localStorage.getItem('isGateway_' + this.device.id);
    this.isGateway = storedIsGateway ? JSON.parse(storedIsGateway) : this.deviceService.getActiveTab();
    localStorage.setItem('isGateway_' + this.device.id, JSON.stringify(this.isGateway));
  }


  public ngOnDestroy(){
    localStorage.removeItem('isGateway_' + this.device.id)
  }

  protected get deviceDetail() {
    if (this.isGateway) {
      return 'Gateway: ' + this.device.id
    } else {
      return 'Device: ' + this.device.id;
    }
  }

  protected get idLabel() {
    if (this.isGateway) {
      return 'Gateway ID: ';
    } else {
      return 'Device ID: ';
    }
  }

  protected getCreationTime(status: any) {
    if (status && status['created']) {
      return this.datePipe.transform(status['created'], 'medium', 'UTC')
    }
    return '-';
  }

  protected deleteDevice(): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the device ' + this.device.id.toBold() + ' ?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete();
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected navigateBack() {
    this.router.navigate(['tenant-detail', this.tenant.id], {
      state: {
        tenant: this.tenant
      }
    });
  }

  protected updateConfig(): void {
    const modalRef = this.modalService.open(UpdateConfigModalComponent, {size: 'lg'});
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully updated config for device ' + this.device.id.toBold());
        this.configs = [res, ...this.configs];
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected sendCommand(): void {
    const modalRef = this.modalService.open(SendCommandComponent, {size: 'lg'});
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully sent command to device ' + this.device.id.toBold());
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected addAuthentication() {
    const modalRef = this.modalService.open(CredentialsModalComponent, {size: 'lg'});
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.credentials = this.credentials;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully added credentials to device ' + this.device.id.toBold());
        this.getCredentials();
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  private delete() {
    this.deviceService.delete(this.device, this.tenant.id).subscribe(() => {
      if(this.isGateway) {
        this.deviceService.listBoundDevices(this.tenant.id, this.gateway.id, this.pageSize, this.pageOffset).subscribe((deviceList) => {
          const boundDevices = deviceList.result;
          boundDevices.forEach((boundDevice: Device) => {
            if (boundDevice.via != null) {
              const index = boundDevice.via.indexOf(this.gateway.id)
              if (index >= 0) {
                boundDevice.via.splice(index, 1);
                this.deviceService.update(boundDevice, this.tenant.id).subscribe((result) => {
                  console.log('update result: ', result);
                });
              }
            }
          });
        });
      }
      this.notificationService.success('Successfully deleted device ' + this.device.id.toBold());
      this.navigateBack();
    }, (error) => {
      console.log('Error deleting device', error);
      this.notificationService.error('Could not delete device ' + this.device.id.toBold());
    })
  }

  private getConfigs() {
    this.configService.list(this.device.id, this.tenant.id).subscribe((configs) => {
      this.configs = configs.deviceConfigs;
    }, (error) => {
      console.log('Error receiving configs for device', error);
    });
  }

  private getCredentials() {
    this.credentials = [];
    this.credentialsService.list(this.device.id, this.tenant.id).subscribe((credentials) => {
      if (credentials && credentials.length > 0) {
        this.credentials = [...credentials];
      }
    }, (error) => {
      console.log('Error receiving credentials for device', error);
    });
  }

  protected bindNewDevicesToGateway(){
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.isBindDeviceFlag = true;
    modalRef.componentInstance.boundDevicesCount = this.boundDeviceListCount;
    modalRef.componentInstance.isGateway = this.isGateway;

    modalRef.componentInstance.devicesSelected.subscribe((selectedDevices: Device[]) => {
      this.boundDevicesList.push(...selectedDevices);
    });
  }

  private setDevices() {
    this.deviceService.listAll(this.tenant.id, this.pageSize, this.pageOffset).subscribe((listResult) => {
      this.devices = listResult.result;
      this.deviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

   protected setBoundDevices() {
    this.deviceService.listBoundDevices(this.tenant.id, this.device.id, this.pageSize, this.pageOffset).subscribe((listResult) => {
      this.boundDevicesList = listResult.result;
      this.boundDeviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

  protected checkIsBoundDevice(){
    if(this.device.via != undefined) {
      this.isBoundDevice = true;
    }
  }
}
