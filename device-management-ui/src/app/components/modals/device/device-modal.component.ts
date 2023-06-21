import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Credentials} from "../../../models/credentials/credentials";
import {Device} from "../../../models/device";
import {DeviceService} from "../../../services/device/device.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-device-modal',
  templateUrl: './device-modal.component.html',
  styleUrls: ['./device-modal.component.scss']
})
export class DeviceModalComponent implements OnInit {

  @Input()
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  protected credentials: Credentials = new Credentials();

  protected devices: Device[] = [];

  protected deviceListCount: number = 0;

  protected sendViaGateway: boolean = false;

  createDevice: boolean = true;

  protected modalTitle: string = 'Create Device';
  protected deviceIdLabel: string = 'Device ID';
  protected confirmButtonLabel: string = 'Save';
  protected sendViaGatewayLabel: string = 'Bind to gateway(s)';
  protected selectDevicesAsGatewayLabel: string = 'Select device(s) as gateway(s).';
  protected selectLabel: string = 'Select devices';
  protected gatewayTooltip: string =
  'Select one or more devices as gateways - <strong>the selected devices will become gateways!</strong> <br /> This will allow the gateways to exchange MQTT/HTTP messages with Eclipse Hono for this device.';
  protected gatewayListCount: number = 0;
  protected gateways: Device[] = [];
  protected devicesAsGateways: Device[] = [];
  protected pageSize: number = 50;
  private pageOffset: number = 0;
  protected gatewayList: string = "already existing gateways";
  protected deviceList: string = "available devices";

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {

  }

  ngOnInit() { }

  protected onClose() {
    this.activeModal.close();
    this.listDevices();
  }

  protected onConfirm() {
    if (this.isInvalid()) {
      return;
    }
    this.device.via = []
    for (const dev of this.selectedDevices) {
      this.device.via?.push(dev.id)
    }
    this.deviceService.create(this.device, this.tenantId).subscribe((result) => {
      if (result) {        
        this.activeModal.close(this.device);
        this.listDevices();
      }
    }, (error) => {
      console.log('Error saving device', this.device.id, error);
      this.notificationService.error('Could not create device for id ' + this.device.id.toBold());
    });
  }
  
  protected isInvalid(): boolean {
    return !this.device || !this.device.id || !this.tenantId ||
      (this.sendViaGateway && (!this.selectedDevices || this.selectedDevices.length === 0));
  }
}
