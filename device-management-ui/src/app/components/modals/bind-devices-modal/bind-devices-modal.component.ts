import {Component, Input, OnInit} from '@angular/core';
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
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  @Input()
  public gatewayId: string = '';

  @Input()
  public bindDevices: boolean = false;

  protected sendViaGateway: boolean = false;
  protected selectedDevices: Device[] = [];
  public devices: Device[] = [];

  public deviceListCount: number = 0;
  private pageOffset: number = 0;
  protected pageSize: number = 50;

  protected modalTitle: string = 'Create Device';
  protected deviceIdLabel: string = 'Device ID';
  protected confirmButtonLabel: string = 'Save';
  protected sendViaGatewayLabel: string = 'Bind to gateway(s)';
  protected bindDevicesLabel: string = 'Bind devices';

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.listDevices();
    this.gateway.id = this.gatewayId;
  }

  protected onClose() {
    this.activeModal.close();
    this.listDevices();
  }

  protected onConfirm() {
    if (this.isInvalid()) {
      return;
    }
    for (const selectedDevice of this.selectedDevices) {
      if (!selectedDevice.via) {
        selectedDevice.via = [];
      }
      if (!selectedDevice.via.includes(this.gateway.id)) {
        selectedDevice.via.push(this.gateway.id);
        this.deviceService.update(selectedDevice, this.tenantId).subscribe((result) => {
          console.log('update result: ', result);
        }, (error) => {
        console.log('Error binding device', this.gateway.id, error);
        this.notificationService.error('Could not bind device to gateway ' + this.gateway.id.toBold());
      });
      }
    }
    this.activeModal.close(this.gateway);
    this.listDevices();
  }

  protected isInvalid(): boolean {
    return  !this.tenantId || (this.sendViaGateway || (!this.selectedDevices || this.selectedDevices.length === 0));
  }

  protected onPageOffsetChanged($event: number) {
    this.pageOffset = $event;
    this.listDevices();
  }

  protected onSelectedDevicesChanged($event: Device[]) {
    this.selectedDevices = $event;
    for (const selectedDevice of this.selectedDevices) {
      this.device.via?.push(selectedDevice.id);
    }
  }

  private listDevices() {
    this.deviceService.listAll(this.tenantId, this.pageSize, this.pageOffset).subscribe((listResult) => { // list all devices except already bounded devices
      this.devices = listResult.result;
      this.deviceListCount = listResult.total;
      console.log('devices refreshed', listResult.total);
    }, (error) => {
      console.log(error);
    });
  }
}
