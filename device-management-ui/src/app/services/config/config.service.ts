import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ConfigRequest} from "../../models/config";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private configUrlSuffix: string = 'v1/configs/:tenantId/:deviceId';

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public list(deviceId: string, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.configUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.get(url, header);
  }

  public updateConfig(deviceId: string, tenantId: string, config: ConfigRequest): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.configUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.post(url, config, header);
  }

}
