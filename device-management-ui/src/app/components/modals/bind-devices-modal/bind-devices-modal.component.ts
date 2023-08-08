import {Component, Input, OnInit, EventEmitter, Output} from '@angular/core';
import {Device} from "../../../models/device";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {DeviceService} from "../../../services/device/device.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-bind-devices-modal',
  templateUrl: './bind-devices-modal.component.html',
  styleUrls: ['./bind-devices-modal.component.scss']
})
export class BindDevicesModalComponent implements OnInit {

  @Input()
  public gateway: Device = new Device();

  @Input()
  public isGateway!: boolean;

  @Input()
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  @Input()
  public deviceId: string = '';

  @Input()
  public boundDevicesCount: number = 0;

  @Input()
  public bindDevices: boolean = false;

  public deviceListCount: number = 0;
  protected sendViaGateway: boolean = false;
  public selectedDevices: Device[] = [];
  public devices: Device[] = [];

  private pageOffset: number = 0;
  protected pageSize: number = 50;

  protected modalTitle: string = 'Create Device';
  protected confirmButtonLabel: string = 'Save';
  protected bindDevicesLabel: string = 'Bind devices';
  protected gatewayTooltip: string =
  'Select one or more devices to bind.<br/>';

  @Output()
  public devicesSelected: EventEmitter<Device[]> = new EventEmitter<Device[]>();

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {
  }

  public ngOnInit(): void {
    this.listAvailableBindingDevices();
  }

  protected onClose() {
    this.activeModal.close();
  }

  public onConfirm() {
    if (this.isInvalid()) {
      return;
    }
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
    this.activeModal.close(this.gateway);
  }

  protected isInvalid(): boolean {
    return  !this.tenantId || (this.sendViaGateway || (!this.selectedDevices || this.selectedDevices.length === 0));
  }

  protected onPageOffsetChanged($event: number) {
    this.pageOffset = $event;
    this.listAvailableBindingDevices();
  }

  protected onSelectedDevicesChanged($event: Device[]) {
    this.selectedDevices = $event;
    for (const selectedDevice of this.selectedDevices) {
      this.device.via?.push(selectedDevice.id);
    }
  }

  private listAvailableBindingDevices() {
    const bindingDeviceId = this.deviceId;
    this.deviceService.listByTenant(this.tenantId, this.pageSize, this.pageOffset, false).subscribe((listResult) => {
      if (this.isGateway) {
        this.deviceListCount = listResult.total - this.boundDevicesCount;
        this.devices = listResult.result.filter((element: Device) => {
          return !element.via?.includes(bindingDeviceId);
        });
      }
      else {
        this.deviceListCount = listResult.total - 1;
        const index = listResult.result.findIndex((object: Device) => {
          return object.id === bindingDeviceId;
        });
        this.devices = listResult.result;
        if (index !== -1) {
          this.devices.splice(index, 1);
        }
      }
    }, (error) => {
      console.log(error);
    });
  }
}
