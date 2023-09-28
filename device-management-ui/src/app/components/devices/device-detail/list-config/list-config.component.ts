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
import {Config} from "../../../../models/config";
import {faCircleCheck, faTriangleExclamation} from '@fortawesome/free-solid-svg-icons';
import {UpdateConfigModalComponent} from "../../../modals/update-config-modal/update-config-modal.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-list-config',
  templateUrl: './list-config.component.html',
  styleUrls: ['./list-config.component.scss']
})
export class ListConfigComponent {

  @Input() public configs: Config[] = [];

  constructor(private modalService: NgbModal) {
  }

  public getDeviceAckTime(config: Config) {
    if (config.deviceAckTime) {
      return faCircleCheck;
    } else {
      return faTriangleExclamation;
    }
  }

  public iconColor(config: Config) {
    if (config.deviceAckTime) {
      return 'color: green';
    } else {
      return 'color: orange';
    }
  }

  showConfig(config: Config) {
    const modalRef = this.modalService.open(UpdateConfigModalComponent, {size: 'lg'});
    modalRef.componentInstance.showConfig = true;
    modalRef.componentInstance.savedConfig = config;

  }
}
