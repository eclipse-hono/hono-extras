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

import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChildren
} from '@angular/core';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeleteComponent} from '../../modals/delete/delete.component';
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {DeviceService} from "../../../services/device/device.service";
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {CreateAndBindModalComponent} from "../../modals/create-and-bind-modal/create-and-bind-modal.component";

@Component({
  selector: 'app-device-list',
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss']
})
export class DeviceListComponent implements OnInit{

  @ViewChildren(SortableTableDirective)
  public sortableHeaders: QueryList<SortableTableDirective> = new QueryList<SortableTableDirective>();

  @Input() public tenant: Tenant = new Tenant();
  @Input() public devices: Device[] = [];
  @Input() public deviceId: string = '';
  @Input() public isGateway!: boolean;
  @Input() public deviceListCount: number = 0;
  @Input() public boundDevicesToGateway: boolean = false;
  @Input() public unbindDevices: boolean = false;

  @Output() public pageSizeChanged: EventEmitter<number> = new EventEmitter<number>();
  @Output() public selectedDevicesChanged: EventEmitter<Device[]> = new EventEmitter<Device[]>();

  public deviceIdLabel: string = 'Device ID';
  public deviceCreatedLabel: string = 'Created (UTC)'
  public searchLabel = '';
  public searchTerm!: string;
  private pageSize: number = 50;
  private selectedDevices: Device[] = [];
  private pageOffset: number = 0;
  private exactSearchString: string = 'Search exact Device ID';
  private searchString = 'Search';

  constructor(private router: Router,
              private modalService: NgbModal,
              private deviceService: DeviceService,
              private sortableTableService: SortableTableService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.searchLabel = this.boundDevicesToGateway ? this.searchString : this.exactSearchString;
    this.listDevices();
  }

  public onSort({ column, direction }: SortEvent) {
    this.sortableHeaders = this.sortableTableService.resetHeaders(this.sortableHeaders, column);
    this.devices = this.sortableTableService.sortItems<Device>(this.devices, {column, direction});
  }

  public getCreationTime(status: any) {
    if (status && status.created) {
      return status.created;
    }
    return '-';
  }

  public selectDevice(device: Device): void {
    this.router.navigate(['device-detail/' + this.tenant.id, device.id], {
      state: {
        tenant: this.tenant,
        device: device
      }
    });
  }

  public createDevice(): void {
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.isDeviceFlag = true;
    modalRef.result.then((device) => {
      if (device) {
        this.listDevices();
        this.notificationService.success("Successfully created device " + device.id.toBold());
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  public deleteDevice(device: Device): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the device ' + device.id.toBold() + '?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete(device);
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  public deviceListIsEmpty(): boolean {
    return !this.devices || this.devices.length === 0;
  }

  public unbindDevicesFromGateway(){
    this.unbindDevices = true;

    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Unbind';
    modalRef.componentInstance.body = 'Do you really want to unbind the selected devices?';
    modalRef.componentInstance.unbind = true;

    modalRef.result.then((res) => {
      if(res) {
        this.unbind();
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  public bindNewDevicesToGateway(){
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.deviceId = this.deviceId;
    modalRef.componentInstance.isBindDeviceFlag = true;
    modalRef.componentInstance.boundDevicesCount = this.deviceListCount;
    modalRef.componentInstance.isGateway = this.isGateway;

    modalRef.componentInstance.devicesSelected.subscribe((selectedDevices: Device[]) => {
      this.devices.push(...selectedDevices);
    });
  }


  public navigateBack() {
    this.router.navigate(['tenant-detail', this.tenant.id], {
      state: {
        tenant: this.tenant
      }
    });
  }

  public devicesSelectedCheck(): boolean{
    return this.devices.some(device => device.checked);
  }

  public markDevice(selectedDevice: Device) {
    selectedDevice.checked = !selectedDevice.checked;
    this.selectedDevices = this.devices.filter(device => device.checked);
    this.selectedDevicesChanged.emit(this.selectedDevices);
  }

  public searchForDevice() {
    if (this.boundDevicesToGateway) {
      return;
    }
    if (!this.searchTerm) {
      this.listDevices()
    } else {
      this.deviceService.getByExactId(this.tenant.id, this.searchTerm).subscribe({
        next: (result) => {
          result.id = this.searchTerm
          this.devices = [result];
          this.deviceListCount = 1;
        },
        error: (_err) => {
          this.notificationService.error('There is no device or gateway with such an ID. The search only finds exact matches.')
        }
      })
    }
  }

  public changePage($event: number) {
    this.pageOffset = ($event -1) * this.pageSize;
    this.listDevices();
  }

  public changePageSize(size: number) {
    this.pageSize = size;
    this.pageOffset = 0;
    this.listDevices();
  }

  private listDevices() {
    if (this.boundDevicesToGateway) {
      return;
    }
    this.deviceService.listByTenant(this.tenant.id, this.pageSize, this.pageOffset, false).subscribe((listResult) => {
      this.devices = listResult.result;
      this.deviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

  private unbind() {
    for (const selectedDevice of this.selectedDevices) {
      const index= selectedDevice.via?.indexOf(this.deviceId);
      if (index !== -1) {
        selectedDevice.via?.splice(index as number, 1);
        for (let i = 0; i < this.devices.length; i++) {
          if (selectedDevice === this.devices[i]) {
            this.devices.splice(i, 1);
            break;
          }
        }
        this.deviceService.update(selectedDevice, this.tenant.id).subscribe(
          () => {
            this.deviceListCount = this.devices.length
            if (this.devices.length <= 0) {
              this.navigateBack();
            }
          },
          (error) => {
            console.log('Error updating device after unbinding: ', selectedDevice, error);
            this.notificationService.error('Could not update device after unbinding');
          }
        );
      }
    }
  }

  private delete(device: Device) {
    this.deviceService.delete(device, this.tenant.id).subscribe(() => {
      const index = this.devices.indexOf(device);
      if (index >= 0) {
        this.devices.splice(index, 1);
        this.deviceListCount = this.deviceListCount -1;
        this.notificationService.success("Successfully deleted device " + device.id.toBold());
      }
    }, (error) => {
      console.log(error);
      this.notificationService.error("Could not delete device " + device.id.toBold());
    })
  }

}
