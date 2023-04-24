import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Tenant} from "../../models/tenant";
import {ApiService} from "../api/api.service";

@Injectable({
  providedIn: 'root'
})

export class TenantService {
  private listTenantUrlSuffix: string = '/v1/tenants/?pageSize=:size&pageOffset=:offset';
  private tenantUrlSuffix: string = '/v1/tenants/:tenantId';

  constructor(private http: HttpClient,
              private apiService: ApiService) {
  }

  public list(size: number, offset: number): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.listTenantUrlSuffix
      .replace(':size', String(size))
      .replace(':offset', String(offset));
    return this.http.get(url, header);
  }

  public create(tenant: Tenant): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.tenantUrlSuffix.replace(':tenantId', tenant.id);
    const requestBody = this.getTenantRequestBody(tenant);
    return this.http.post(url, requestBody, header);
  }

  public update(tenant: Tenant): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.tenantUrlSuffix.replace(':tenantId', tenant.id);
    const requestBody = this.getTenantRequestBody(tenant);
    return this.http.put(url, requestBody, header);
  }

  public delete(tenantId: string): Observable<any> {
    const header = this.apiService.getHttpsRequestOptions();
    const url = this.tenantUrlSuffix.replace(':tenantId', tenantId);
    return this.http.delete(url, header);
  }

  private getTenantRequestBody(tenant: Tenant) {
    return {
      "ext": {
        "messaging-type": tenant.ext["messaging-type"]
      }
    }
  }

}
