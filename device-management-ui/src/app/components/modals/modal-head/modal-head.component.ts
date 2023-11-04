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

@Component({
  selector: 'app-modal-head',
  templateUrl: './modal-head.component.html',
  styleUrls: ['./modal-head.component.scss']
})
export class ModalHeadComponent {

  @Input() public modalTitle: string = '';

  @Output() public closeModal: EventEmitter<boolean> = new EventEmitter<boolean>();

  public cancel() {
    this.closeModal.emit(true);
  }

}
