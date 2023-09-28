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

import {Component, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Command} from "../../../models/command";
import {CommandService} from "../../../services/command/command.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-send-command',
  templateUrl: './send-command.component.html',
  styleUrls: ['./send-command.component.scss']
})
export class SendCommandComponent {

  @Input() public deviceId: string = '';
  @Input() public tenantId: string = '';

  public correlationId: number | undefined;
  public requestResponseCommandEnabled: boolean = false;
  public correlationIdEnabled: boolean = false;
  public useText: boolean = true;
  public command: Command = new Command();

  constructor(private activeModal: NgbActiveModal,
              private commandService: CommandService,
              private notificationService: NotificationService) {
  }

  public onChange($event: any) {
    this.useText = $event.target.value == 'text';
  }

  public onConfirm() {
    if (!this.command || !this.command.binaryData || !this.deviceId || !this.tenantId) {
      return;
    }
    this.updateCommandData();
    this.commandService.sendCommand(this.deviceId, this.tenantId, this.command).subscribe(() => {
      this.activeModal.close(this.command);
    }, (error) => {
      console.log(error);
      this.command.binaryData = atob(this.command.binaryData);
      this.notificationService.error('Could not send command to device '+ this.deviceId.toBold() + '. Reason: ' + error.error.error);
    });
  }

  public onChangeRequestResponseCommandEnabled(requestResponseCommandChecked: boolean) {
    if (!requestResponseCommandChecked) {
      this.correlationIdEnabled = false;
      this.correlationId = undefined;
      this.command['response-required'] = this.requestResponseCommandEnabled;
      this.command['correlation-id'] = this.correlationId;
    }
  }

  public onChangeCorrelationIdEnabled(correlationIdChecked: boolean) {
    if (!correlationIdChecked) {
      this.correlationId = undefined;
      this.command['correlation-id'] = this.correlationId;
    }
  }

  private updateCommandData() {
    if (this.useText) {
      this.command.binaryData = btoa(this.command.binaryData);
    }

    if (this.requestResponseCommandEnabled) {
      this.command['response-required'] = this.requestResponseCommandEnabled;
    }

    if (this.correlationIdEnabled) {
      this.command['correlation-id'] = this.correlationId;
    }
  }

  public onClose() {
    this.activeModal.close();
  }



}
