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
import {Config, ConfigRequest} from "../../../models/config";
import {ConfigService} from "../../../services/config/config.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-update-config-modal',
  templateUrl: './update-config-modal.component.html',
  styleUrls: ['./update-config-modal.component.scss']
})

export class UpdateConfigModalComponent implements OnInit{

  @Input() public deviceId: string = '';
  @Input() public tenantId: string = '';
  @Input() public showConfig: boolean = false;
  @Input() public savedConfig: Config = new Config();

  public modalTitle: string = 'Update config';
  public body: string = 'Enter the new configuration below. If you\'re using MQTT, the configuration will be propagated to the device when it connects.';
  public useText: boolean = true;
  public config: ConfigRequest = new ConfigRequest();
  public textToShow: string = '';
  private textString: string = 'text';
  private savedConfigString: string = 'Saved configuration';

  constructor(private activeModal: NgbActiveModal,
              private configService: ConfigService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    if (this.showConfig && this.savedConfig.binaryData != '') {
      this.config = this.savedConfig;
      this.modalTitle = this.savedConfigString;
      this.textToShow = atob(this.config.binaryData);
    }
  }

  public onChange($event: any) {
    if ($event.target.value === this.textString) {
      this.textToShow = atob(this.config.binaryData);
    } else {
      this.textToShow = this.config.binaryData;
    }
    this.useText = $event.target.value == 'text';
  }

  public onConfirm() {
    if (!this.config || !this.config.binaryData || !this.deviceId || !this.tenantId) {
      return;
    }
    this.updateConfigData();
    this.configService.updateConfig(this.deviceId, this.tenantId, this.config).subscribe((result: Config) => {
      if (result) {
        this.activeModal.close(result);
      }
    }, (error) => {
      console.log('Error updating config', error);
      this.notificationService.error('Could not update config for device ' + this.deviceId.toBold() + '. Reason: ' + error.error.error);
    })
  }

  public onClose() {
    this.activeModal.close();
  }

  public updateConfig() {
    if (this.useText) {
      this.config.binaryData = btoa(this.textToShow);
    } else {
      this.config.binaryData = this.textToShow;
    }
  }

  private updateConfigData() {
    if (this.useText) {
      this.config.binaryData = btoa(this.textToShow);
    }
  }


}
