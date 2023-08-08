import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
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
  public gateway: Device = new Device();

  @Input()
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  public devices: Device[] = [];
  public selectedDevices: Device[] = [];
  protected createDevice: boolean = false;
  public deviceListCount: number = 0;
  private pageOffset: number = 0;
  protected pageSize: number = 50;
  protected modalTitle: string = 'Create Gateway';
  protected gatewayIdLabel: string = 'Gateway ID';
  protected confirmButtonLabel: string = 'Save';

  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {

  }

  public ngOnInit() {
    this.listDevices();
  }

  protected onClose() {
    this.activeModal.close();
  }

  public onConfirm() {
    if (this.isInvalid()) {
      return;
    }
    this.deviceService.create(this.gateway, this.tenantId).subscribe((result) => {
      if (result) {
        for (const selectedDevice of this.selectedDevices) {
          if (!selectedDevice.via) {
            selectedDevice.via = [];
          }
          if (!selectedDevice.via.includes(this.gateway.id)) {
            selectedDevice.via.push(this.gateway.id);
            this.deviceService.update(selectedDevice, this.tenantId).subscribe(() => {
            });
          }

        }
        this.activeModal.close(this.gateway);
      }
    }, (error) => {
      console.log('Error saving device', this.gateway.id, error);
      this.notificationService.error('Could not create device for id ' + this.gateway.id.toBold());
    });


  }

  public isInvalid(): boolean {
    return !this.gateway || !this.gateway.id || !this.tenantId || !this.selectedDevices || this.selectedDevices.length === 0;
  }

  protected onPageOffsetChanged($event: number) {
    this.pageOffset = $event;
    this.listDevices();
  }

  public onSelectedDevicesChanged($event: Device[]) {
    this.selectedDevices = $event;
    for (const selectedDevice of this.selectedDevices) {
      this.device.via?.push(selectedDevice.id);
    }
  }

  private listDevices() {
    this.deviceService.listByTenant(this.tenantId, this.pageSize, this.pageOffset, false).subscribe((listResult) => {
      this.devices = listResult.result;
      this.deviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }
}
