import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Credentials} from "../../../models/credentials/credentials";
import {Device} from "../../../models/device";
import {DeviceService} from "../../../services/device/device.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-gateway-modal',
  templateUrl: './gateway-modal.component.html',
  styleUrls: ['./gateway-modal.component.scss']
})
export class GatewayModalComponent implements OnInit {

  @Input()
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  @Input()
  public allDevices: Device[] = [];

  protected credentials: Credentials = new Credentials();

  protected devices: Device[] = [];

  protected sendViaGateway: boolean = false;

  protected modalTitle: string = 'Create Gateway';
  protected deviceIdLabel: string = 'Gateway ID';
  protected confirmButtonLabel: string = 'Save';
  protected selectDevicesAsGatewayLabel: string = 'Select devices';
  protected selectLabel: string = 'Select Device';

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {

  }

  ngOnInit() {
    this.getDevicesToSendVia(this.allDevices);
  }

  protected onClose() {
    this.activeModal.close();
  }

  protected onConfirm() {
    if (this.isInvalid()) {
      return;
    }
    this.deviceService.save(this.device, this.tenantId).subscribe((result) => {
      if (result) {
        this.activeModal.close(this.device);
      }
    }, (error) => {
      console.log('Error saving device', this.device.id, error);
      this.notificationService.error('Could not create device for id ' + this.device.id.toBold());
    });
  }

  protected isInvalid(): boolean {
    return !this.device || !this.device.id || !this.tenantId ||
      (this.sendViaGateway && (!this.device.via || this.device.via.length === 0));
  }

  private getDevicesToSendVia(devices: Device[]) {
    for (const device of devices) {
      if (device.via && device.via.length > 0) {
        continue;
      }
      this.devices = [device, ...this.devices];
    }
  }

}
