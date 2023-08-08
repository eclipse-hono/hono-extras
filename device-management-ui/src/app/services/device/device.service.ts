import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ApiService} from "../api/api.service";
import {Device} from "../../models/device";

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  private listWithIsGatewayFilterUrlSuffix: string = '/v1/devices/:tenantId/?pageSize=:size&pageOffset=:offset&isGateway=:isGateway';
  private listWithJsonFilterUrlSuffix: string = '/v1/devices/:tenantId/?pageSize=:size&pageOffset=:offset&filterJson=:filter';
  private listWithoutFilterUrlSuffix: string = '/v1/devices/:tenantId/?pageSize=:size&pageOffset=:offset';
  private deviceUrlSuffix: string = '/v1/devices/:tenantId/:deviceId';
  private isGateway: boolean = false;

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public listByTenant(tenantId: string, size: number, offset: number, onlyGateways: boolean): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.listWithIsGatewayFilterUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':size', String(size))
      .replace(':offset', String(offset))
      .replace(':isGateway', String(onlyGateways));
    return this.http.get(url, header);
  }

  public listBoundDevices(tenantId: string, gatewayId: string, size: number, offset: number): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const filter = this.getBoundDevicesFilter(gatewayId);
    const url = this.listWithJsonFilterUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':size', String(size))
      .replace(':offset', String(offset))
      .replace(':filter', String(filter));
    return this.http.get(url, header);
  }

  public listAll(tenantId: string, size: number, offset: number): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.listWithoutFilterUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':size', String(size))
      .replace(':offset', String(offset))
    return this.http.get(url, header);
  }

  public create(device: Device, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.deviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', device.id);
    const body = device.via ? JSON.stringify({
      "via": device.via
    }) : {};
    return this.http.post(url, body, header);
  }

  public update(device: Device, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    let body = {};
    if (device.enabled != null && device.via != null) {
      body = JSON.stringify({
        "via":
          device.via
        ,
        "enabled": device.enabled
      });
    } else  if (device.via != null) {
      body = JSON.stringify({
        "via":
          device.via
      });
    }
    const url = this.deviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', device.id);
    return this.http.put(url, body, header);
  }

  public delete(device: Device, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.deviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', device.id);
    return this.http.delete(url, header);
  }


  private getBoundDevicesFilter(gatewayId: string) {
    return `{"field": "/via","value": "*\\"${gatewayId}\\"*"}`;
  }

  public setActiveTab(isGateway:boolean){
    this.isGateway = isGateway;
  }
  public getActiveTab() : boolean{
    return this.isGateway;
  }
}
