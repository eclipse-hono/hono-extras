import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})
export class StatesService {

  private statesUrlSuffix: string = 'v1/states/:tenantId/:deviceId';

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public list(deviceId: string, tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.statesUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.get(url, header);
  }
}
