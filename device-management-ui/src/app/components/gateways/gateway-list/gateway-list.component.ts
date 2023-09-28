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

import {Component, Input, QueryList, ViewChildren} from '@angular/core';
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {DeviceService} from "../../../services/device/device.service";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {DeleteComponent} from "../../modals/delete/delete.component";
import {CreateAndBindModalComponent} from "../../modals/create-and-bind-modal/create-and-bind-modal.component";

@Component({
  selector: 'app-gateway-list',
  templateUrl: './gateway-list.component.html',
  styleUrls: ['./gateway-list.component.scss']
})
export class GatewayListComponent {

  @ViewChildren(SortableTableDirective)
  public sortableHeaders: QueryList<SortableTableDirective> = new QueryList<SortableTableDirective>();

  @Input() public tenant: Tenant = new Tenant();

  public gatewayListCount: number = 0;
  public searchTerm!: string;
  public pageSize: number = 50;
  public gateways: Device[] = [];
  private pageOffset: number = 0;

  constructor(private router: Router,
              private modalService: NgbModal,
              private deviceService: DeviceService,
              private sortableTableService: SortableTableService,
              private notificationService: NotificationService) {
  }

  public ngOnInit() {
    this.listGateways();
  }

  public searchForGateway() {
    if (!this.searchTerm) {
      this.listGateways()
    } else {
      this.deviceService.getByExactId(this.tenant.id, this.searchTerm).subscribe({
        next: (result) => {
          result.id = this.searchTerm
          this.gateways = [result];
          this.gatewayListCount = 1;
        },
        error: (_err) => {
          this.notificationService.error('There is no device or gateway with such an ID. The search only finds exact matches.')
        }
      })
    }
  }

  public changePage($event: number) {
    this.pageOffset = ($event - 1) * this.pageSize;
    this.listGateways();
  }

  public changePageSize(size: number) {
    this.pageSize = size;
    this.pageOffset = 0;
    this.listGateways();
  }

  public onSort({column, direction}: SortEvent) {
    this.sortableHeaders = this.sortableTableService.resetHeaders(this.sortableHeaders, column);
    this.gateways = this.sortableTableService.sortItems<Device>(this.gateways, {column, direction});
  }

  public getCreationTime(status: any) {
    if (status && status['created']) {
      return status['created'];
    }
    return '-';
  }

  public selectGateway(gateway: Device): void {
    this.router.navigate(['device-detail/' + this.tenant.id, gateway.id], {
      state: {
        tenant: this.tenant,
        device: gateway,
        isGateway: true,
      },
    });
  }

  public createGateway(): void {
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.isGatewayFlag = true;
    modalRef.result.then((gateway) => {
      if (gateway) {
        this.gateways = [...this.gateways,gateway]
        this.notificationService.success("Successfully created gateway " + gateway.id.toBold());
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  public deleteGateway(gateway: Device): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the gateway ' + gateway.id.toBold() + '?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete(gateway);
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  public gatewayListIsEmpty(): boolean {
    return !this.gateways || this.gateways.length === 0;
  }

  private listGateways() {
    this.deviceService.listByTenant(this.tenant.id, this.pageSize, this.pageOffset, true).subscribe((listResult) => {
      this.gateways = listResult.result;
      this.gatewayListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

  private delete(gateway: Device) {
    this.deviceService.delete(gateway, this.tenant.id).subscribe(() => {

      this.deviceService.listBoundDevices(this.tenant.id, gateway.id, this.pageSize, this.pageOffset).subscribe((deviceList) => {
        const boundDevices = deviceList.result;
        boundDevices.forEach((boundDevice: Device) => {
          if (boundDevice.via != null) {
            const index = boundDevice.via.indexOf(gateway.id)
            if (index >= 0) {
              boundDevice.via.splice(index, 1);

              this.deviceService.update(boundDevice, this.tenant.id).subscribe((result) => {
                console.log('update result: ', result);
              });
            }
          }
        });
      });

      const index = this.gateways.indexOf(gateway);
      if (index >= 0) {
        this.gateways.splice(index, 1);
        this.gatewayListCount = this.gatewayListCount - 1;
        this.notificationService.success("Successfully deleted gateway " + gateway.id.toBold());
      }
    }, (error) => {
      console.log(error);
      this.notificationService.error("Could not delete gateway " + gateway.id.toBold());
    })
  }
}
