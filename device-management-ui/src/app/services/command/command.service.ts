import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Command} from "../../models/command";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})
export class CommandService {

  private commandUrlSuffix: string = 'v1/commands/:tenantId/:deviceId'

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public sendCommand(deviceId: string, tenantId: string, command: Command): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.commandUrlSuffix
      .replace(':tenantId', tenantId)
      .replace(':deviceId', deviceId);
    return this.http.post(url, command, header);
  }
}
