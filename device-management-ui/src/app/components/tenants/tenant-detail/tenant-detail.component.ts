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
import {Router} from "@angular/router";
import {Tenant} from "../../../models/tenant";
import {TenantModalComponent} from '../../modals/tenant/tenant-modal.component';
import {DeviceService} from "../../../services/device/device.service";
import {TenantService} from "../../../services/tenant/tenant.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {Device} from "../../../models/device";

@Component({
  selector: 'app-tenant-detail',
  templateUrl: './tenant-detail.component.html',
  styleUrls: ['./tenant-detail.component.scss']
})
export class TenantDetailComponent {

  public tenant: Tenant = new Tenant();
  public deviceListCount: number = 0;
  public pageSize: number = 50;
  public devices: Device[] = [];

  constructor(private modalService: NgbModal,
              private deviceService: DeviceService,
              private tenantService: TenantService,
              private router: Router,
              private notificationService: NotificationService) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation) {
      const state = navigation.extras.state
      if (state && state['tenant']) {
        this.tenant = state['tenant'];
        this.setActiveTab(false);
      }
    }
  }

  protected get tenantDetail() {
    return 'Tenant: ' + this.tenant.id;
  }

  protected get messagingType() {
    if (this.tenant.ext && this.tenant.ext['messaging-type']) {
      return this.tenant.ext['messaging-type'];
    }
    return '-';
  }

  protected navigateBack() {
    this.router.navigate(['tenant-list']);
  }

  protected editTenant(): void {
    const modalRef = this.modalService.open(TenantModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenant = this.tenant;
    modalRef.componentInstance.isNewTenant = false;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully edited tenant ' + this.tenant.id.toBold());
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected deleteTenant(): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Delete Tenant';
    modalRef.componentInstance.body = 'Do you really want to delete the tenant ' + this.tenant.id.toBold() + '?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete();
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  private delete() {
    this.tenantService.delete(this.tenant.id).subscribe(() => {
      this.notificationService.success('Successfully deleted tenant ' + this.tenant.id.toBold());
      this.navigateBack();
    }, (error) => {
      console.log(error);
      this.notificationService.error('Could not delete tenant ' + this.tenant.id.toBold());
    })
  }

  public setActiveTab(isGateway : boolean){
    this.deviceService.setActiveTab(isGateway);
  }
}
