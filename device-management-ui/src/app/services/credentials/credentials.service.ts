import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Credentials} from "../../models/credentials/credentials";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})
export class CredentialsService {

  private credentialsUrlSuffix: string = '/v1/credentials/:tenantId/:deviceId';

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public save(deviceId: string, tenantId: string, credentials: Credentials[]): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.credentialsUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.put(url, credentials, header);
  }

  public list(deviceId: string, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.credentialsUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.get(url, header);
  }

}
