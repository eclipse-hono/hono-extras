import {Component, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Config, ConfigRequest} from "../../../models/config";
import {ConfigService} from "../../../services/config/config.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-update-config-modal',
  templateUrl: './update-config-modal.component.html',
  styleUrls: ['./update-config-modal.component.scss']
})
export class UpdateConfigModalComponent {

  @Input()
  public deviceId: string = '';

  @Input()
  public tenantId: string = '';

  protected modalTitle: string = 'Update config';
  protected body: string = 'Enter the new configuration below. If you\'re using MQTT, the configuration will be propagated to the device when it connects.';
  protected configLabel: string = 'Configuration';
  protected confirmButtonLabel: string = 'Update';
  protected textLabel: string = 'Text';
  protected base64Label: string = 'Base 64';
  protected formatLabel: string = 'Format';
  protected useText: boolean = true;

  protected config: ConfigRequest = new ConfigRequest();

  constructor(private activeModal: NgbActiveModal,
              private configService: ConfigService,
              private notificationService: NotificationService) {
  }

  protected onChange($event: any) {
    this.useText = $event.target.value == 'text';
  }

  protected onConfirm() {
    if (!this.config || !this.config.binaryData || !this.deviceId || !this.tenantId) {
      return;
    }
    this.updateConfigData();
    this.configService.updateConfig(this.deviceId, this.tenantId, this.config).subscribe((result: Config) => {
      if (result) {
        this.activeModal.close(result);
      }
    }, (error) => {
      console.log('Error updating config', error);
      this.notificationService.error('Could not update config for device ' + this.deviceId.toBold() + '. Reason: ' + error.error.error);
    })
  }

  protected onClose() {
    this.activeModal.close();
  }

  private updateConfigData() {
    if (this.useText) {
      this.config.binaryData = btoa(this.config.binaryData);
    }
  }

}
