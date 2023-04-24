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

  @Input()
  public isNewCredentials: boolean = true;

  @Input()
  public deviceId: string = '';

  @Input()
  public tenantId: string = '';

  @Input()
  public credential: Credentials  = new Credentials();

  @Input()
  public credentials: Credentials[] = [];

  protected confirmButtonLabel: string = 'Save';
  protected authIdLabel: string = 'Auth ID';
  protected authenticationTypeLabel: string = 'Authentication type';
  protected authIdTooltip: string = 'The Auth ID must be unique for this tenant and the selected authentication type.';
  protected authTypes: {
    key: string,
    value: string,
  }[] = [
    {key: CredentialTypes.HASHED_PASSWORD, value: 'Password based'},
    {key: CredentialTypes.RPK, value: 'JWT based'},
  ];
  protected modalTitle: string = '';
  protected authType: string = '';
  protected authId: string = '';

  protected publicKeyHeader: string = '-----BEGIN PUBLIC KEY-----';
  protected publicKeyFooter: string = '-----END PUBLIC KEY-----';
  protected certHeader: string = '-----BEGIN CERTIFICATE-----';
  protected certFooter: string = '-----END CERTIFICATE-----';

  constructor(private activeModal: NgbActiveModal,
              private credentialsService: CredentialsService,
              private notificationService: NotificationService) {

  }

  protected get isPassword() {
    return this.authType === CredentialTypes.HASHED_PASSWORD;
  }

  protected get isRpk() {
    return this.authType === CredentialTypes.RPK;
  }

  ngOnInit() {
    if (!this.isNewCredentials && this.credential && this.credential["auth-id"] && this.credential.type) {
      this.modalTitle = 'Update Credentials';
      this.authId = this.credential["auth-id"];
      this.authType = this.credential.type;
    } else {
      this.modalTitle = 'Add Credentials';
    }
  }

  protected setSecret($event: any) {
    this.credential.secrets = [$event];
  }

  protected onClose() {
    this.activeModal.close();
  }

  protected onConfirm() {
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
        this.notificationService.error("Could not save credentials.")
      });
    }
  }

  protected trimKey(credential: Credentials) {
    credential.secrets[0].key =
      credential.secrets[0].key?.replaceAll(this.publicKeyHeader,'')?.replaceAll(this.publicKeyFooter,'')?.replaceAll(/\n/g, '');

    credential.secrets[0].cert =
    credential.secrets[0].cert?.replaceAll(this.certHeader,'')?.replaceAll(this.certFooter,'')?.replaceAll(/\n/g, '');
  }

  protected isInvalid(): boolean {
    if (!this.deviceId || !this.tenantId) {
      return true;
    }
    return !this.isAuthenticationValid();
  }

  private isAuthenticationValid(): boolean {
    return !!this.credential.secrets[0] && !!this.authId && !!this.authType;
  }

}
