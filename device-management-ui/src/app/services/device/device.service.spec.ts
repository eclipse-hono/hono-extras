import {TestBed} from '@angular/core/testing';

import {DeviceService} from './device.service';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../api/api.service";
import {of} from "rxjs";
import {Device} from "../../models/device";

describe('DeviceService', () => {
  let service: DeviceService;
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
    service = TestBed.inject(DeviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return list of devices when listByTenant is called with filter', () => {
    const result: Device[] = [new Device(), new Device(), new Device()];
    apiServiceSpy.getHttpsRequestOptions.and.returnValue({
      headers: {},
      withCredentials: true,
      observe: 'body' as 'response'
    });
    httpClientSpy.get.and.returnValue(of(result));

    service.listByTenant('test-tenant', 50, 1).subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.length).toEqual(3);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    const expectedUrl: string = '/v1/devices/test-tenant/?pageSize=50&pageOffset=1&filterJson={"field": "/gateways","value": false}';
    expect(httpClientSpy.get).toHaveBeenCalledWith(expectedUrl, {
      headers: {},
      withCredentials: true,
      observe: 'body' as 'response'
    });
  });

  it('should return list of gateways when listByTenant is called with filter', () => {
    const result: Device[] = [new Device(), new Device(), new Device()];
    apiServiceSpy.getHttpsRequestOptions.and.returnValue({
      headers: {},
      withCredentials: true,
      observe: 'body' as 'response'
    });
    httpClientSpy.get.and.returnValue(of(result));

    service.listByTenant('test-tenant', 50, 1, true).subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.length).toEqual(3);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    const expectedUrl: string = '/v1/devices/test-tenant/?pageSize=50&pageOffset=1&filterJson={"field": "/gateways","value": true}';
    expect(httpClientSpy.get).toHaveBeenCalledWith(expectedUrl, {
      headers: {},
      withCredentials: true,
      observe: 'body' as 'response'
    });
  });

  it('should return list of all devices when listAll is called', () => {
    const result: Device[] = [new Device(), new Device(), new Device()];
    apiServiceSpy.getHttpsRequestOptions.and.returnValue({
      headers: {},
      withCredentials: true,
      observe: 'body' as 'response'
    });
    httpClientSpy.get.and.returnValue(of(result));

    service.listAll('test-tenant', 10, 1).subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.length).toEqual(3);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    const expectedUrl: string = '/v1/devices/test-tenant/?pageSize=10&pageOffset=1';
    expect(httpClientSpy.get).toHaveBeenCalledWith(expectedUrl, {
      headers: {},
      withCredentials: true,
      observe: 'body' as 'response'
    });
  });

  it('should return created device when save is called', () => {
    const deviceId: string = 'test-device';
    const device: Device = new Device();
    device.id = deviceId;
    httpClientSpy.post.and.returnValue(of(device));

    service.create(device,'test-tenant').subscribe((success) => {
      expect(success).toEqual(device);
      expect(success.id).toEqual(deviceId);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.post).toHaveBeenCalled();
  });

  it('should return updated device when update is called', () => {
    const deviceId: string = 'test-device';
    const device: Device = new Device();
    device.id = deviceId;
    httpClientSpy.put.and.returnValue(of(device));

    service.update(device,'test-tenant').subscribe((success) => {
      expect(success).toEqual(device);
      expect(success.id).toEqual(deviceId);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.put).toHaveBeenCalled();
  });

  it('should return body of true when delete is called', () => {
    httpClientSpy.delete.and.returnValue(of(true));

    service.delete('device-id','test-tenant').subscribe((success) => {
      expect(success).toEqual(true);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.delete).toHaveBeenCalled();
  });
});
