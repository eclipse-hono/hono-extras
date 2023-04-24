import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Credentials, CredentialTypes} from "../../../../models/credentials/credentials";
import {AuthenticationValue} from "../../../../models/authentication-value";
import {CredentialsService} from "../../../../services/credentials/credentials.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CredentialsModalComponent} from "../../../modals/credentials-modal/credentials-modal.component";
import {DeleteComponent} from "../../../modals/delete/delete.component";
import {NotificationService} from "../../../../services/notification/notification.service";

@Component({
  selector: 'app-list-authentication',
  templateUrl: './list-authentication.component.html',
  styleUrls: ['./list-authentication.component.scss']
})
export class ListAuthenticationComponent implements OnInit, OnChanges {

  @Input()
  public deviceId: string = '';

  @Input()
  public tenantId: string = '';

  @Input()
  public credentials: Credentials[] = [];

  protected authenticationTypeLabel: string = 'Authentication type';
  protected authIdLabel: string = 'Auth ID';
  protected secretIdLabel: string = 'Secret ID';
  protected expiryDateLabel: string = 'Expiry time (UTC)';
  protected actionsLabel: string = 'Actions';
  protected editLabel: string = 'Edit';
  protected editDisabledLabel: string = 'Edit not possible due to authentication method';
  protected deleteLabel: string = 'Delete';
  protected authenticationValues: AuthenticationValue[] = [];
  private authIdKey: string = 'auth-id';
  private notBeforeKey: string = 'not-before';
  private notAfterKey: string = 'not-after';

  constructor(private modalService: NgbModal,
              private credentialsService: CredentialsService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.setAuthenticationValues(this.credentials);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['credentials'].currentValue) {
      this.setAuthenticationValues(this.credentials);
    }
  }

  protected isEditable(authenticationValue: AuthenticationValue): boolean {
    return authenticationValue.type === CredentialTypes.RPK;
  }

  protected edit(authenticationValue: AuthenticationValue) {
    if (!this.isEditable(authenticationValue)) {
      return;
    }
    const modalRef = this.modalService.open(CredentialsModalComponent, {size: 'lg'});
    modalRef.componentInstance.isNewCredentials = false;
    modalRef.componentInstance.tenantId = this.tenantId;
    modalRef.componentInstance.deviceId = this.deviceId;
    modalRef.componentInstance.credential = this.findCredentials(authenticationValue);
    modalRef.componentInstance.credentials = this.credentials;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success("Successfully edited credentials of device " + this.deviceId.toBold());
      }
      this.getCredentials();
    });
  }

  protected delete(authenticationValue: AuthenticationValue) {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the credentials ' + authenticationValue[this.authIdKey] + '?';
    modalRef.result.then((res) => {
      if (res) {
        const credential: Credentials = this.findCredentials(authenticationValue);
        if (credential) {
          const removed: boolean = this.removeCredential(credential);
          if (removed) {
            this.saveCredentials(authenticationValue);
          }
        }
      }
    });
  }

  protected getAuthenticationType(type: CredentialTypes | string | undefined): string {
    if (!type) {
      return '-';
    }
    if (type == CredentialTypes.RPK) {
      return 'JWT based'
    } else {
      return 'Password based';
    }
  }

  private saveCredentials(authenticationValue: AuthenticationValue) {
    this.credentialsService.save(this.deviceId, this.tenantId, this.credentials).subscribe(() => {
      const index = this.authenticationValues.indexOf(authenticationValue);
      if (index >= 0) {
        this.authenticationValues.splice(index, 1);
        this.notificationService.success("Successfully deleted credentials for device " + this.deviceId.toBold());
      }
    }, (error) => {
      console.log('Error saving credentials for device', this.deviceId, error);
      this.notificationService.error("Could not delete credentials for device " + this.deviceId.toBold());
    })
  }

  private removeCredential(credential: Credentials): boolean {
    const index = this.credentials.indexOf(credential);
    if (index >= 0) {
      this.credentials.splice(index, 1);
      return true;
    }
    return false;
  }

  private findCredentials(authenticationValue: AuthenticationValue): Credentials {
    for (const credential of this.credentials) {
      if (credential[this.authIdKey] === authenticationValue[this.authIdKey]) {
        return credential;
      }
    }
    return new Credentials();
  }

  private getCredentials() {
    this.credentialsService.list(this.deviceId, this.tenantId).subscribe((credentials) => {
      if (credentials && credentials.length > 0) {
        this.credentials = credentials;
        this.setAuthenticationValues(credentials);
      }
    }, (error) => {
      console.log(error);
    })
  }


  private setAuthenticationValues(credentials: any[]) {
    this.authenticationValues = [];
    for (const c of credentials) {
      for (const secret of c.secrets) {
        const authenticationValue: AuthenticationValue = new AuthenticationValue();
        authenticationValue.id = secret.id;
        authenticationValue.type = c.type;
        authenticationValue[this.authIdKey] = c[this.authIdKey];
        authenticationValue[this.notAfterKey] = secret[this.notAfterKey];
        authenticationValue[this.notBeforeKey] = secret[this.notBeforeKey];
        authenticationValue.algorithm = secret.algorithm;
        authenticationValue.key = secret.key;

        this.authenticationValues = [authenticationValue, ...this.authenticationValues];
      }
    }
  }
}
