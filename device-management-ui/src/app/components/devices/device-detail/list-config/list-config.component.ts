import {Component, Input} from '@angular/core';
import {Config} from "../../../../models/config";
import {faCircleCheck, faTriangleExclamation} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-list-config',
  templateUrl: './list-config.component.html',
  styleUrls: ['./list-config.component.scss']
})
export class ListConfigComponent {

  @Input()
  public configs: Config[] = [];

  protected versionLabel: string = 'Version';
  protected updateLabel: string = 'Cloud update time (UTC)';
  protected dataLabel: string = 'Data';
  protected acknowledgementLabel: string = 'Ack.';

  constructor() {
  }

  public getDeviceAckTime(config: Config) {
    if (config.deviceAckTime) {
      return faCircleCheck;
    } else {
      return faTriangleExclamation;
    }
  }

  public iconColor(config: Config) {
    if (config.deviceAckTime) {
      return 'color: green';
    } else {
      return 'color: orange';
    }
  }
}
