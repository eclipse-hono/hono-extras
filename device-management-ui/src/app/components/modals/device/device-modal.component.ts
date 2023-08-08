import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
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

  @Input()
  public bindDevices: boolean = false;
  protected sendViaGateway: boolean = false;
  public selectedDevices: Device[] = [];
  public devices: Device[] = [];
  private pageOffset: number = 0;
  protected pageSize: number = 50;

  protected createDevice: boolean = true;

  protected modalTitle: string = 'Create Device';
  protected deviceIdLabel: string = 'Device ID';
  protected confirmButtonLabel: string = 'Save';
  protected sendViaGatewayLabel: string = 'Bind to gateway(s)';

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {
  }

  public ngOnInit(): void {
    this.listDevices();
    }

  protected onClose() {
    this.activeModal.close();
  }

  public onConfirm() {
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
    this.deviceService.listAll(this.tenantId, this.pageSize, this.pageOffset).subscribe((listResult) => {
      const filteredList = listResult.result.filter((element: any) => {
        return !element.via
      })
      this.devices = filteredList;
    });
  }
}
