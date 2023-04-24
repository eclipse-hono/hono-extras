import {Component} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeleteComponent} from '../../modals/delete/delete.component';
import {Device} from "../../../models/device";
import {Router} from "@angular/router";
import {Tenant} from "../../../models/tenant";
import {UpdateConfigModalComponent} from '../../modals/update-config-modal/update-config-modal.component';
import {SendCommandComponent} from '../../modals/send-command/send-command.component';
import {DeviceService} from "../../../services/device/device.service";
import {ConfigService} from "../../../services/config/config.service";
import {Config} from "../../../models/config";
import {CredentialsModalComponent} from '../../modals/credentials-modal/credentials-modal.component';
import {Credentials} from 'src/app/models/credentials/credentials';
import {CredentialsService} from "../../../services/credentials/credentials.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-device-detail',
  templateUrl: './device-detail.component.html',
  styleUrls: ['./device-detail.component.scss']
})
export class DeviceDetailComponent {
  protected idLabel: string = 'Device ID: ';
  protected tenantIdLabel: string = 'Tenant ID:';
  protected creationTimeLabel: string = 'Created (UTC):';
  protected configLabel: string = 'Configuration';
  protected stateLabel: string = 'State'
  protected authenticationLabel: string = 'Authentication'
  protected device: Device = new Device();
  protected tenant: Tenant = new Tenant();
  protected deleteLabel: string = 'Delete';
  protected updateLabel: string = 'Update Config';
  protected sendLabel: string = 'Send Command';
  protected addAuthenticationLabel: string = 'Add Credentials';

  protected configs: Config[] = [];
  protected credentials: Credentials[] = [];

  constructor(private modalService: NgbModal,
              private router: Router,
              private deviceService: DeviceService,
              private configService: ConfigService,
              private credentialsService: CredentialsService,
              private notificationService: NotificationService,
              private datePipe: DatePipe) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation) {
      const state = navigation.extras.state
      if (state && state['tenant']) {
        this.tenant = state['tenant'];
      }

      if (state && state['device']) {
        this.device = state['device'];
      }

    }
    this.getConfigs();
    this.getCredentials();
  }

  protected get deviceDetail() {
    return 'Device: ' + this.device.id;
  }

  protected getCreationTime(status: any) {
    if (status && status['created']) {
      return this.datePipe.transform(status['created'], 'medium', 'UTC')
    }
    return '-';
  }

  protected deleteDevice(): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the device ' + this.device.id.toBold() + ' ?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete();
      }
    });
  }

  protected navigateBack() {
    this.router.navigate(['tenant-detail', this.tenant.id], {
      state: {
        tenant: this.tenant
      }
    });
  }

  protected updateConfig(): void {
    const modalRef = this.modalService.open(UpdateConfigModalComponent, {size: 'lg'});
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully updated config for device ' + this.device.id.toBold());
        this.configs = [res, ...this.configs];
      }
    });
  }

  protected sendCommand(): void {
    const modalRef = this.modalService.open(SendCommandComponent, {size: 'lg'});
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully sent command to device ' + this.device.id.toBold());
      }
    })
  }

  protected addAuthentication() {
    const modalRef = this.modalService.open(CredentialsModalComponent, {size: 'lg'});
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.credentials = this.credentials;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully added credentials to device ' + this.device.id.toBold());
        this.getCredentials();
      }
    });
  }

  private delete() {
    this.deviceService.delete(this.device.id, this.tenant.id).subscribe(() => {
      this.notificationService.success('Successfully deleted device ' + this.device.id.toBold());
      this.navigateBack();
    }, (error) => {
      console.log('Error deleting device', error);
      this.notificationService.error('Could not delete device ' + this.device.id.toBold());
    })
  }

  private getConfigs() {
    this.configService.list(this.device.id, this.tenant.id).subscribe((configs) => {
      this.configs = configs.deviceConfigs;
    }, (error) => {
      console.log('Error receiving configs for device', error);
    });
  }

  private getCredentials() {
    this.credentials = [];
    this.credentialsService.list(this.device.id, this.tenant.id).subscribe((credentials) => {
      if (credentials && credentials.length > 0) {
        this.credentials = [...credentials];
      }
    }, (error) => {
      console.log('Error receiving credentials for device', error);
    });
  }


}
