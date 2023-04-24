import {Component, Input} from '@angular/core';
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
export class DeviceModalComponent {

  @Input()
  public device: Device = new Device();

  @Input()
  public tenantId: string = '';

  protected credentials: Credentials = new Credentials();

  protected modalTitle: string = 'Create Device';
  protected deviceIdLabel: string = 'Device ID';
  protected confirmButtonLabel: string = 'Save';


  constructor(private activeModal: NgbActiveModal,
              private deviceService: DeviceService,
              private notificationService: NotificationService) {

  }

  protected onClose() {
    this.activeModal.close();
  }

  protected onConfirm() {
    if (!this.device || !this.device.id || !this.tenantId) {
      return;
    }

    this.deviceService.save(this.device.id, this.tenantId).subscribe((result) => {
      if (result) {
        this.activeModal.close(this.device);
      }
    }, (error) => {
      console.log('Error saving device', this.device.id, error);
      this.notificationService.error('Could not create device for id ' + this.device.id.toBold());
    });
  }

  protected isInvalid(): boolean {
    return !this.device || !this.device.id || !this.tenantId;
  }

}
