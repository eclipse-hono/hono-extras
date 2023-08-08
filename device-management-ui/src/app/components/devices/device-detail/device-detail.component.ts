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
import { BindDevicesModalComponent } from '../../modals/bind-devices-modal/bind-devices-modal.component';

@Component({
  selector: 'app-device-detail',
  templateUrl: './device-detail.component.html',
  styleUrls: ['./device-detail.component.scss']
})
export class DeviceDetailComponent {


  public isGateway: boolean = false;
  public bindDevices: boolean = false;
  protected tenantIdLabel: string = 'Tenant ID:';
  protected creationTimeLabel: string = 'Created (UTC):';
  protected configLabel: string = 'Configuration';
  protected stateLabel: string = 'State'
  protected authenticationLabel: string = 'Authentication'
  protected device: Device = new Device();
  protected gateway: Device = new Device();
  protected tenant: Tenant = new Tenant();
  public devices: Device[] = [];
  public boundDevicesList: Device[] = [];
  protected deleteLabel: string = 'Delete';
  protected updateLabel: string = 'Update Config';
  protected sendLabel: string = 'Send Command';
  protected addAuthenticationLabel: string = 'Add Credentials';
  protected boundDevices: string = 'Bound Devices';
  protected viaLabel: string = 'Via:';
  protected configs: Config[] = [];
  protected credentials: Credentials[] = [];
  protected deviceListCount: number = 0;
  protected boundDeviceListCount: number = 0;
  protected pageSize: number = 50;
  private pageOffset: number = 0;
  protected isBoundDevice: boolean = false;

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
        this.setDevices();
        const storedIsGateway = localStorage.getItem('isGateway_' + this.device.id);
        this.isGateway = storedIsGateway ? JSON.parse(storedIsGateway) : this.deviceService.getActiveTab();
        localStorage.setItem('isGateway_' + this.device.id, JSON.stringify(this.isGateway));
      }

    }
    if(this.isGateway) {
      this.gateway = this.device;
    }
    this.getConfigs();
    this.getCredentials();
    this.checkIsBoundDevice();
  }

  public ngOnDestroy(){
    localStorage.removeItem('isGateway_' + this.device.id)
  }

  protected get deviceDetail() {
    if (this.isGateway) {
      return 'Gateway: ' + this.device.id
    } else {
      return 'Device: ' + this.device.id;
    }
  }

  protected get idLabel() {
    if (this.isGateway) {
      return 'Gateway ID: ';
    } else {
      return 'Device ID: ';
    }
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
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
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
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
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
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
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
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  private delete() {
    this.deviceService.delete(this.device, this.tenant.id).subscribe(() => {
      if(this.isGateway) {
        this.deviceService.listBoundDevices(this.tenant.id, this.gateway.id, this.pageSize, this.pageOffset).subscribe((deviceList) => {
          const boundDevices = deviceList.result;
          boundDevices.forEach((boundDevice: Device) => {
            if (boundDevice.via != null) {
              const index = boundDevice.via.indexOf(this.gateway.id)
              if (index >= 0) {
                boundDevice.via.splice(index, 1);
                this.deviceService.update(boundDevice, this.tenant.id).subscribe((result) => {
                  console.log('update result: ', result);
                });
              }
            }
          });
        });
      }
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

  protected openBindDevicesModal(){
    const modalRef = this.modalService.open(BindDevicesModalComponent, {size: 'lg'});

    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.bindDevices = true;
    modalRef.componentInstance.deviceId = this.device.id;
    modalRef.componentInstance.isGateway = this.isGateway;
    modalRef.componentInstance.boundDevicesCount = this.boundDeviceListCount;

    modalRef.componentInstance.devicesSelected.subscribe((selectedDevices: Device[]) => {
      this.boundDevicesList.push(...selectedDevices);
    });
  }

  private setDevices() {
    this.deviceService.listAll(this.tenant.id, this.pageSize, this.pageOffset).subscribe((listResult) => {
      this.devices = listResult.result;
      this.deviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

   protected setBoundDevices() {
    this.deviceService.listBoundDevices(this.tenant.id, this.device.id, this.pageSize, this.pageOffset).subscribe((listResult) => {
      this.boundDevicesList = listResult.result;
      this.boundDeviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

  protected checkIsBoundDevice(){
    if(this.device.via != undefined) {
      this.isBoundDevice = true;
    }
  }
}
