import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Device} from "../../../models/device";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {DeviceService} from "../../../services/device/device.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-create-and-bind-modal',
  templateUrl: './create-and-bind-modal.component.html',
  styleUrls: ['./create-and-bind-modal.component.scss']
})
export class CreateAndBindModalComponent implements OnInit{

  @Input()
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  @Input()
  public isDeviceFlag: boolean = false;

  @Input()
  public isGatewayFlag: boolean = false;

  @Input()
  public deviceId: string = '';

  @Input()
  public isGateway: boolean = false;

  @Input()
  public isBindDeviceFlag: boolean = false;

  @Input()
  public boundDevicesCount: number = 0;

  @Output()
  public devicesSelected: EventEmitter<Device[]> = new EventEmitter<Device[]>();
  public sendViaGateway: boolean = false;
  public selectedDevices: Device[] = [];
  public devices: Device[] = [];
  public modalTitle: string = '';
  public deviceListCount: number = 0;
  public pageSize: number = 50;
  public selectLabel: string = '';
  public gatewayTooltip: string =
    'Select one or more devices to bind.<br/>';
  private pageOffset: number = 0;

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {
  }

  public ngOnInit(): void {
    this.setModalTitle();
    this.listDevices();
  }

  public onClose() {
    this.activeModal.close();
  }

  public onConfirm() {
    if (this.isInvalid()) {
      return;
    }

    if (this.isDeviceFlag) {
      this.createDevice();
    } else if (this.isGatewayFlag) {
      this.createGateway();
    } else if (this.isBindDeviceFlag) {
      this.bindDevice();
    }
  }

  public onPageOffsetChanged($event: number) {
    this.pageOffset = $event;
    this.listDevices();
  }

  public onSelectedDevicesChanged($event: Device[]) {
    this.selectedDevices = $event;
    for (const selectedDevice of this.selectedDevices) {
      this.device.via?.push(selectedDevice.id);
    }
  }

  public isInvalid(): boolean {
    const checkDeviceAndTenant = !this.device?.id || !this.tenantId;
    const checkSendViaGateway = this.sendViaGateway && (!this.selectedDevices || this.selectedDevices.length === 0);
    if(this.isDeviceFlag) {
      return checkDeviceAndTenant || checkSendViaGateway;
    } else if(this.isGatewayFlag) {
      return checkDeviceAndTenant || !this.selectedDevices || this.selectedDevices.length === 0;
    } else {
      return !this.tenantId || checkSendViaGateway;
    }
  }

  private listDevices() {
    let onlyGateways = false;

    if(this.isDeviceFlag) {
      this.selectLabel = 'Select gateway(s).';
      onlyGateways = true;
    } else if (this.isGatewayFlag || this.isBindDeviceFlag) {
      this.selectLabel = 'Select device(s).';
      this.sendViaGateway = true;
    }

    this.deviceService.listByTenant(this.tenantId, this.pageSize, this.pageOffset, onlyGateways).subscribe((listResult) => {
      if(this.isBindDeviceFlag) {
        this.listAvailableBindingDevices(listResult);
      } else {
        this.devices = listResult.result;
        this.deviceListCount = listResult.total;
      }
    }, (error) => {
      console.log(error);
    });

  }

  private listAvailableBindingDevices(listResult: any) {
      if (this.isGateway) {
        this.deviceListCount = listResult.total - this.boundDevicesCount;
        this.devices = listResult.result.filter((element: Device) => {
          return !element.via?.includes(this.deviceId);
        });
      }
      else {
        this.deviceListCount = listResult.total - 1;
        const index = listResult.result.findIndex((object: Device) => {
          return object.id === this.deviceId;
        });
        this.devices = listResult.result;
        if (index !== -1) {
          this.devices.splice(index, 1);
        }
      }
  }

  private createDevice() {
    this.device.via = [];
    for (const dev of this.selectedDevices) {
      this.device.via?.push(dev.id)
    }
    this.deviceService.create(this.device, this.tenantId).subscribe((result) => {
      if (result) {
        this.activeModal.close(this.device);
      }
    }, (error) => {
      console.log('Error saving device', this.device.id, error);
      this.notificationService.error('Could not create device for id ' + this.device.id.toBold());
    });
  }

  private createGateway() {
    this.deviceService.create(this.device, this.tenantId).subscribe((result) => {
      if (result) {
        for (const selectedDevice of this.selectedDevices) {
          if (!selectedDevice.via) {
            selectedDevice.via = [];
          }
          if (!selectedDevice.via.includes(this.device.id)) {
            selectedDevice.via.push(this.device.id);
            this.deviceService.update(selectedDevice, this.tenantId).subscribe(() => {
            }, (error) => {
              console.log('Error saving gateway', this.device.id, error);
              this.notificationService.error('Could not create gateway for id ' + this.device.id.toBold());
            });
          }
        }
        this.activeModal.close(this.device);
      }
    }, (error) => {
      console.log('Error saving gateway', this.device.id, error);
      this.notificationService.error('Could not create gateway for id ' + this.device.id.toBold());
    });
  }

  private bindDevice() {
    for (const selectedDevice of this.selectedDevices) {
      if (!selectedDevice.via) {
        selectedDevice.via = [];
      }
      if (!selectedDevice.via.includes(this.deviceId)) {
        selectedDevice.via.push(this.deviceId);
        this.deviceService.update(selectedDevice, this.tenantId).subscribe((result) => {
          console.log('update result: ', result);
        }, (error) => {
          console.log('Error binding device', this.deviceId, error);
          this.notificationService.error('Could not bind device to gateway ' + this.deviceId.toBold());
        });
      }
      selectedDevice.checked = false;
    }
    const selectedDevices: Device [] = this.selectedDevices;
    this.devicesSelected.emit(selectedDevices);
    this.activeModal.close(this.device);
  }

  private setModalTitle() {
    if (this.isDeviceFlag) {
      this.modalTitle = 'Create new device';
    } else if (this.isGatewayFlag) {
      this.modalTitle = 'Create new gateway';
    } else if (this.isBindDeviceFlag) {
      this.modalTitle = 'Bind device(s)';
    }
  }
}
