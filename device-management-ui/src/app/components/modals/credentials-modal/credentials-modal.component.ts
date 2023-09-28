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
import {Credentials, CredentialTypes} from "../../../models/credentials/credentials";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {CredentialsService} from "../../../services/credentials/credentials.service";
import {NotificationService} from "../../../services/notification/notification.service";
@Component({
  selector: 'app-credentials-modal',
  templateUrl: './credentials-modal.component.html',
  styleUrls: ['./credentials-modal.component.scss']
})
export class CredentialsModalComponent implements OnInit {

  @Input() public isNewCredentials: boolean = true;
  @Input() public deviceId: string = '';
  @Input() public tenantId: string = '';
  @Input() public credential: Credentials  = new Credentials();
  @Input() public credentials: Credentials[] = [];

  public isSecretInvalid: boolean = false;
  public authTypes: {
    key: string,
    value: string,
  }[] = [
    {key: CredentialTypes.HASHED_PASSWORD, value: 'Password based'},
    {key: CredentialTypes.RPK, value: 'JWT based'},
  ];
  public modalTitle: string = '';
  public authType: string = '';
  public authId: string = '';

  private publicKeyHeader: string = '-----BEGIN PUBLIC KEY-----';
  private publicKeyFooter: string = '-----END PUBLIC KEY-----';
  private certHeader: string = '-----BEGIN CERTIFICATE-----';
  private certFooter: string = '-----END CERTIFICATE-----';
  private usePublicKey: boolean = true;

  constructor(private activeModal: NgbActiveModal,
              private credentialsService: CredentialsService,
              private notificationService: NotificationService) {

  }

  public get isPassword() {
    return this.authType === CredentialTypes.HASHED_PASSWORD;
  }

  public get isRpk() {
    return this.authType === CredentialTypes.RPK;
  }

  public ngOnInit() {
    if (!this.isNewCredentials && this.credential && this.credential["auth-id"] && this.credential.type) {
      this.modalTitle = 'Update Credentials';
      this.authId = this.credential["auth-id"];
      this.authType = this.credential.type;
    } else {
      this.modalTitle = 'Add Credentials';
    }
  }

  public setSecret($event: any) {
    if ($event === undefined) return;

    this.usePublicKey = $event.usePublicKey;
    if (this.usePublicKey == undefined) {
      this.isSecretInvalid = this.handlePasswordBasedSecretValidity($event);
    } else {
      this.isSecretInvalid = this.handleJWTBasedSecretValidity($event);
    }
  }

  public onClose() {
    this.activeModal.close();
  }

  public onConfirm() {
    if (!this.deviceId || !this.tenantId) {
      return;
    }
    if (this.isAuthenticationValid()) {
      if (this.isNewCredentials) {
        this.credential["auth-id"] = this.authId;
        this.credential.type = this.authType;
        this.credentials.push(this.credential);
      }
      this.trimKey(this.credential);
      this.cleanCert();
      this.credentialsService.save(this.deviceId, this.tenantId, this.credentials).subscribe(() => {
        this.activeModal.close(this.credentials);
      }, (error) => {
        if (this.isNewCredentials) {
          const index = this.credentials.indexOf(this.credential);
          if (index >= 0) {
            this.credentials.splice(index, 1);
          }
        }
        console.log('Error adding credentials for device', this.deviceId, error);
        this.notificationService.error("Could not save credentials. Please check your inputs again.")
      });
    }
  }

  public trimKey(credential: Credentials) {
    if (credential.secrets[0].key) {
      credential.secrets[0].key =
        credential.secrets[0].key?.replaceAll(this.publicKeyHeader,'')?.replaceAll(this.publicKeyFooter,'')?.replaceAll(/\n/g, '');
    }

    if (credential.secrets[0].cert) {
      credential.secrets[0].cert =
      credential.secrets[0].cert?.replaceAll(this.certHeader,'')?.replaceAll(this.certFooter,'')?.replaceAll(/\n/g, '');
    }
   }

  public isInvalid(): boolean {
    if (!this.deviceId || !this.tenantId) {
      return true;
    }
    return !this.isAuthenticationValid();
  }

  public onChangeAuthOrPasswordType() {
    this.isSecretInvalid = true;
    this.credential.secrets = [];
  }

  private isAuthenticationValid(): boolean {
    return !!this.credential.secrets[0] && !!this.authId && !!this.authType && !this.isSecretInvalid;
  }

  private cleanCert() {
    if (!this.usePublicKey) {
      delete this.credential.secrets[0].algorithm;
      delete this.credential.secrets[0].key;
    } else {
      delete this.credential.secrets[0].cert;
    }
  }

  private handlePasswordBasedSecretValidity($event: any) {
    if ($event["pwd-plain"]?.length === 0 || $event["pwd-hash"]?.length === 0 || $event["hash-function"]?.length === 0) {
      return true;
    } else {
      this.credential.secrets = [$event];
      return false;
    }
  }

  private handleJWTBasedSecretValidity($event: any) {
    if ((this.usePublicKey && $event.secret?.key.length === 0) || (!this.usePublicKey && !$event.secret?.cert) || $event.secret == undefined) {
        return true;
      } else {
        this.credential.secrets = [$event.secret];
        return false;
      }
  }
}
