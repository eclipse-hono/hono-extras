import {Component, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Command} from "../../../models/command";
import {CommandService} from "../../../services/command/command.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-send-command',
  templateUrl: './send-command.component.html',
  styleUrls: ['./send-command.component.scss']
})
export class SendCommandComponent {

  @Input()
  public deviceId: string = '';

  @Input()
  public tenantId: string = '';

  protected modalTitle: string = 'Send command';
  protected body: string = 'Enter a once-off directive in the field below. Devices must be connected to MQTT and subscribed to the commands topic at the time that your directive is sent.';
  protected binaryDataLabel: string = 'Command Binary Data';
  protected subfolderLabel: string = 'Command Subfolder';
  protected deleteButtonLabel: string = 'Send';
  protected textLabel: string = 'Text';
  protected base64Label: string = 'Base 64';
  protected formatLabel: string = 'Format';
  protected useText: boolean = true;

  protected command: Command = new Command();

  constructor(private activeModal: NgbActiveModal,
              private commandService: CommandService,
              private notificationService: NotificationService) {
  }

  protected onChange($event: any) {
    this.useText = $event.target.value == 'text';
  }

  protected onConfirm() {
    if (!this.command || !this.command.binaryData || !this.deviceId || !this.tenantId) {
      return;
    }

    this.updateCommandData();
    this.commandService.sendCommand(this.deviceId, this.tenantId, this.command).subscribe(() => {
      this.activeModal.close(this.command);
    }, (error) => {
      console.log(error);
      this.notificationService.error('Could not send command to device '+ this.deviceId.toBold() + '. Reason: ' + error.error.error);
    });
  }

  private updateCommandData() {
    if (this.useText) {
      this.command.binaryData = btoa(this.command.binaryData);
    }
  }

  protected onClose() {
    this.activeModal.close();
  }

}
