import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ApiService} from "../api/api.service";
import {Device} from "../../models/device";

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  private listDeviceUrlSuffix: string = '/v1/devices/:tenantId/?pageSize=:size&pageOffset=:offset';
  private listDeviceWithoutFilterUrlSuffix: string = '/v1/devices/:tenantId';
  private deviceUrlSuffix: string = '/v1/devices/:tenantId/:deviceId';

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public listByTenant(tenantId: string, size: number, offset: number): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.listDeviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':size', String(size))
      .replace(':offset', String(offset));
    return this.http.get(url, header);
  }

  public create(device: Device, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.deviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', device.id);
    const body = device.via ? {'via' : device.via} : {};
    return this.http.post(url, body, header);
  }

  public update(device: Device, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.deviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', device.id);
    const body = device.via ? {'via': device.via} : {};
    return this.http.put(url, body, header);
  }

  public delete(deviceId: string, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.deviceUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.delete(url, header);
  }

  public listAll(tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.listDeviceWithoutFilterUrlSuffix
      .replace(':tenantId', tenantId);
    return this.http.get(url, header);
  }
}
