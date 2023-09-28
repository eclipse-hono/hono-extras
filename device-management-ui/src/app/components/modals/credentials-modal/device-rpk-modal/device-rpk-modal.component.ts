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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Secret} from "../../../../models/credentials/secret";

@Component({
  selector: 'app-device-rpk-modal',
  templateUrl: './device-rpk-modal.component.html',
  styleUrls: ['./device-rpk-modal.component.scss']
})
export class DeviceRpkModalComponent implements OnInit {

  @Input() public rpkSecret: Secret = new Secret();
  @Input() public isNewCredentials: boolean = true;

  @Output() public rpkSecretChanged: EventEmitter<{secret: Secret|undefined, usePublicKey: boolean}> = new EventEmitter<{secret: Secret|undefined, usePublicKey: boolean}>();

  public usePublicKey: boolean = true;
  public notBefore: boolean = false;
  public notAfter: boolean = false;
  public algorithmTypes: string[] = ['EC', 'RSA'];

  public ngOnInit() {
    if (!this.rpkSecret) {
      this.rpkSecret = new Secret();
    }
    if (this.rpkSecret["not-before"]) {
      this.notBefore = true;
    }
    if (this.rpkSecret["not-after"]) {
      this.notAfter = true;
    }
  }

  public setUsePublicKey(usePublicKey: boolean) {
    this.usePublicKey = usePublicKey;
    this.rpkSecretChanged.emit({secret: this.rpkSecret, usePublicKey: usePublicKey});
  }

  public onRpkSecretChanged() {
    if (this.isInvalid()) {
      this.rpkSecretChanged.emit({secret: undefined, usePublicKey: this.usePublicKey});
    } else {
      this.rpkSecretChanged.emit({secret: this.rpkSecret, usePublicKey: this.usePublicKey});
    }
  }

  public setNotBeforeDateTime($event: any) {
    this.rpkSecret["not-before"] = this.setDateTime($event);
    this.onRpkSecretChanged();
  }

  public setNotAfterDateTime($event: any) {
    this.rpkSecret["not-after"] = this.setDateTime($event);
    this.onRpkSecretChanged();
  }

  public onChangeNotBefore() {
    if (!this.notBefore) {
      delete this.rpkSecret["not-before"];
    }
  }

  public onChangeNotAfter() {
    if (!this.notAfter) {
      delete this.rpkSecret["not-after"];
    }
  }

  private setDateTime($event: any): string | undefined {
    if (!$event || !$event.date || !$event.time || $event.time.hour === undefined || $event.time.minute === undefined || $event.time.second === undefined) {
      return undefined;
    }

    const date = new Date($event.date.year, $event.date.month, $event.date.day);
    date.setHours(Number($event.time.hour));
    date.setMinutes(Number($event.time.minute));
    date.setSeconds($event.time.second);
    return date.toISOString();
  }

  private isInvalid(): boolean {
    if (this.usePublicKey) {
      return !this.rpkSecret.key || !this.rpkSecret.algorithm;
    } else {
      return !this.rpkSecret.cert;
    }
  }

}
