import {TestBed} from '@angular/core/testing';

import {TenantService} from './tenant.service';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../api/api.service";
import {of} from "rxjs";
import {Tenant} from "../../models/tenant";

describe('TenantService', () => {
  let service: TenantService;
  let httpClientSpy: {
    get: jasmine.Spy;
    post: jasmine.Spy;
    put: jasmine.Spy;
    delete: jasmine.Spy;
  };
  let apiServiceSpy: {
    getHttpsRequestOptions: jasmine.Spy;
  };

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'post', 'put', 'delete']);
    apiServiceSpy = jasmine.createSpyObj('ApiService', ['getHttpsRequestOptions']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue: httpClientSpy},
        {provide: ApiService, useValue: apiServiceSpy}
      ]
    });
    service = TestBed.inject(TenantService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return list of 4 tenants when list is called', () => {
    const result: Tenant[] = [new Tenant(), new Tenant(), new Tenant(), new Tenant()];
    httpClientSpy.get.and.returnValue(of(result));

    service.list(50, 1).subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.length).toEqual(4);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.get).toHaveBeenCalled();
  });

  it('should return created tenant when create is called', () => {
    const request: Tenant = new Tenant();
    request.id = 'test-tenant';
    request.ext = {'messaging-type': 'pubsub'};
    httpClientSpy.post.and.returnValue(of(request));

    service.create(request).subscribe((success) => {
      expect(success).toEqual(request);
      expect(success.ext['messaging-type']).toEqual('pubsub');
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.post).toHaveBeenCalled();
  });

  it('should return updated tenant when update is called', () => {
    const request: Tenant = new Tenant();
    request.id = 'test-tenant';
    request.ext = {'messaging-type': 'kafka'};
    httpClientSpy.put.and.returnValue(of(request));

    service.update(request).subscribe((success) => {
      expect(success).toEqual(request);
      expect(success.ext['messaging-type']).toEqual('kafka');
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.put).toHaveBeenCalled();
  });

  it('should body of true when delete is called', () => {
    httpClientSpy.delete.and.returnValue(of(true));

    service.delete('test-tenant').subscribe((success) => {
      expect(success).toEqual(true);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.delete).toHaveBeenCalled();
  });
});
