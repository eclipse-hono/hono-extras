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

  protected sendViaGateway: boolean = false;

  protected modalTitle: string = 'Create Device';
  protected deviceIdLabel: string = 'Device ID';
  protected confirmButtonLabel: string = 'Save';
  protected sendViaGatewayLabel: string = 'Send via gateway';
  protected selectDevicesAsGatewayLabel: string = 'Select devices as gateways';
  protected selectLabel: string = 'Select Device';
  protected gatewayTooltip: string = 'Select devices as gateways to bind to this device. This will allow the gateway(s) to exchange MQTT/HTTP messages with Eclipse Hono for this device.';

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {

  }

  ngOnInit() {
    this.listDevices();
  }

  protected onClose() {
    this.activeModal.close();
  }

  protected onConfirm() {
    if (this.isInvalid()) {
      return;
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

  protected isInvalid(): boolean {
    return !this.device || !this.device.id || !this.tenantId ||
      (this.sendViaGateway && (!this.device.via || this.device.via.length === 0));
  }

  private listDevices() {
    //TODO add backend logic call
    const listResult = {
      result: [{
        id: "device1",
        status: {
          created: "2023-03-15T16:05:19Z"
        }
      }, {
        id: "device2",
        status: {
          created: "2023-03-15T16:05:19Z"
        }
      },
        {
          id: "device3",
          status: {
            created: "2023-03-15T16:05:19Z"
          }
        },
        {
          id: "device4",
          status: {
            created: "2023-03-15T16:05:19Z"
          }
        }, {
          id: "device5",
          status: {
            created: "2023-03-15T16:05:19Z"
          }
        }],
      total: 5,
    }
    this.devices = listResult.result;
  }

}
