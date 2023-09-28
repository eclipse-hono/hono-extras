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

import {Component, EventEmitter, Output} from '@angular/core';
import {Secret} from "../../../../models/credentials/secret";

@Component({
  selector: 'app-device-password-modal',
  templateUrl: './device-password-modal.component.html',
  styleUrls: ['./device-password-modal.component.scss']
})
export class DevicePasswordModalComponent {

  @Output() public passwordSecretChanged: EventEmitter<Secret> = new EventEmitter<Secret>();
  @Output() public passwordSecretTypeChanged: EventEmitter<boolean> = new EventEmitter<boolean>();

  public passwordSecret: Secret = new Secret();
  public usePlainPassword: boolean = true;

  public setUsePlainPassword(usePlainPassword: boolean) {
    this.usePlainPassword = usePlainPassword;
    this.passwordSecret = new Secret();
    this.passwordSecretChanged.emit(undefined);
    this.passwordSecretTypeChanged.emit(true);
  }

  public onPasswordSecretChanged() {
    if (this.isInvalid()) {
      this.passwordSecretChanged.emit(undefined);
    } else {
      delete this.passwordSecret.algorithm;
      delete this.passwordSecret.key;
      delete this.passwordSecret.cert;
      if (this.usePlainPassword) {
        delete this.passwordSecret["pwd-hash"];
        delete this.passwordSecret["hash-function"];
        delete this.passwordSecret.salt;
      } else {
        delete this.passwordSecret["pwd-plain"];
      }
      this.passwordSecretChanged.emit(this.passwordSecret);
    }
  }

  private isInvalid(): boolean {
    if (this.usePlainPassword) {
      return !this.passwordSecret["pwd-plain"];
    } else {
      return !this.passwordSecret["pwd-hash"] || !this.passwordSecret["hash-function"];
    }
  }
}
