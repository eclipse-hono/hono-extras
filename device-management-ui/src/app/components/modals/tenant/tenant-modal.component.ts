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

import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Tenant} from "../../../models/tenant";
import {TenantService} from "../../../services/tenant/tenant.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-tenant-modal',
  templateUrl: './tenant-modal.component.html',
  styleUrls: ['./tenant-modal.component.scss'],

})
export class TenantModalComponent implements OnInit {

  @Input() public isNewTenant: boolean = true;
  @Input() public tenant: Tenant = new Tenant();

  public tenantIdLabel = 'Tenant ID';
  public modalTitle: string = '';
  public messagingTypes: {
    key: string,
    value: string,
  }[] = [
    {key: 'pubsub', value: 'Pub/Sub'},
    {key: 'kafka', value: 'Kafka'},
    {key: 'amqp', value: 'AMQP'}
  ]

  constructor(private activeModal: NgbActiveModal,
              private tenantService: TenantService,
              private notificationService: NotificationService) {
  }

  public ngOnInit() {
    if (this.isNewTenant) {
      this.modalTitle = 'Create Tenant';
    } else {
      this.modalTitle = 'Edit Tenant';
    }
    if (!this.tenant.ext) {
      this.tenant.ext = {'messaging-type': ''};
    }
  }

  public onConfirm() {
    if (this.isInvalid()) {
      return;
    }
    if (this.isNewTenant) {
      this.createTenant();
    } else {
      this.updateTenant();
    }
  }

  public onClose() {
    this.activeModal.close();
  }

  public isInvalid() {
    return !this.tenant || !this.tenant.id || !this.tenant.ext || !this.tenant.ext['messaging-type'];
  }

  private createTenant() {
    this.tenantService.create(this.tenant).subscribe((result) => {
      if (result) {
        this.activeModal.close(this.tenant);
      }
    }, (error) => {
      console.log('Error saving tenant ', this.tenant.id, error);
      this.notificationService.error('Could not create tenant');
    });
  }

  private updateTenant() {
    this.tenantService.update(this.tenant).subscribe(() => {
      this.activeModal.close(this.tenant);
    }, (error) => {
      console.log('Error saving tenant ' + this.tenant.id, error);
      this.notificationService.error('Could not update tenant');
    });
  }
}
