import {Component, Input, OnInit} from '@angular/core';
import {Config} from "../../../../../models/config";

@Component({
  selector: 'app-config-accordion',
  templateUrl: './config-accordion.component.html',
  styleUrls: ['./config-accordion.component.scss']
})
export class ConfigAccordionComponent implements OnInit {

  @Input()
  public config: Config = new Config();

  protected fullConfig: string = '';

  protected showTextLabel: string = 'show text';

  protected showText: boolean = false;

  ngOnInit() {
    this.fullConfig = this.config.binaryData;
  }

  protected showTextData(showText: boolean) {
    if (showText) {
      this.fullConfig = atob(this.config.binaryData);
    } else {
      this.fullConfig = this.config.binaryData;
    }
  }

}
