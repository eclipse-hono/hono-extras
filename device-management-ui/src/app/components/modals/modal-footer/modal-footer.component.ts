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
  selector: 'app-modal-footer',
  templateUrl: './modal-footer.component.html',
  styleUrls: ['./modal-footer.component.scss']
})
export class ModalFooterComponent {

  @Input() public confirmButtonLabel: string = '';
  @Input() public buttonDisabled: boolean = false;
  @Input() public showRequiredFieldInfo: boolean = true;

  @Output() public confirmButtonPressed: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() public cancelButtonPressed: EventEmitter<boolean> = new EventEmitter<boolean>();

  public confirm() {
    this.confirmButtonPressed.emit(true);
  }

  public cancel() {
    this.cancelButtonPressed.emit(true);
  }
}
