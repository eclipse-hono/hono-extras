import { Component, Input, OnInit } from '@angular/core';
import {Device} from "../../../models/device";
import { DeviceService } from 'src/app/services/device/device.service';
import { Tenant } from 'src/app/models/tenant';

@Component({
  selector: 'app-device-as-gateway',
  templateUrl: './device-as-gateway.component.html',
  styleUrls: ['./device-as-gateway.component.scss']
})
export class DeviceAsGatewayComponent implements OnInit {

  @Input()
  public device: Device = new Device();

  @Input()
  public sendViaGateway!: boolean;

  @Input()
  public tenantId: string = '';

  public devices: Device[] = [];
  public deviceListCount: number = 0;

  protected gateways: Device[] = [];
  
  protected pageSize: number = 50;
  private pageOffset: number = 0;
  
  protected searchTerm!: string;

  protected searchLabel: string = 'Search';
  protected selectDevicesAsGatewayLabel: string = 'Select device(s) as gateway(s).';
  protected selectedDevicesList: string = "List of selected devices"
  protected gatewayTooltip: string = 
  'Select one or more devices as gateways - <strong>the selected devices will become gateways!</strong> <br /> This will allow the gateways to exchange MQTT/HTTP messages with Eclipse Hono for this device.';

  constructor (
    private deviceService: DeviceService,
    ) { }

  ngOnInit(): void {
      this.listAll();
  }

  protected markAsGateway(device: Device) {
    if (!this.device.via) {
      this.device.via = [];
    }
    device.checked = true;
    if (!this.device.via?.includes(device.id)) {      
      this.device.via?.push(device.id);
    }
  }

  protected unmarkAsGateway(newGateway: string) {
    const indexvia = this.device.via?.indexOf(newGateway);
    if (indexvia != undefined && indexvia >= 0) {
      this.device.via?.splice(indexvia, 1);
    }
  }

  protected deviceListIsEmpty(): boolean {
    return !this.devices || this.devices.length === 0;
  }

  protected gateWayListIsEmpty(): boolean {
    return !this.device.via || this.device.via.length === 0;
  }

  protected changePage($event: number) {
    this.pageOffset = ($event -1) * this.pageSize;
    this.listAll();
  }

  private listAll() {
    this.deviceService.listAll(this.tenantId, this.pageSize, this.pageOffset).subscribe((listResult) => {
      this.devices = listResult.result;
      this.deviceListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }
}
