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
    delete: jasmine.Spy;
  }
  let apiServiceSpy: {
    getHttpsRequestOptions: jasmine.Spy;
  }

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'post', 'delete']);
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

  it('should return list of devices when listByTenant is called', () => {
    const result: Device[] = [new Device(), new Device(), new Device()];
    httpClientSpy.get.and.returnValue(of(result));

    service.listByTenant('test-tenant', 50, 1).subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.length).toEqual(3);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.get).toHaveBeenCalled();
  });

  it('should return created device when save is called', () => {
    const deviceId: string = 'test-device';
    const result: Device = new Device();
    result.id = deviceId;
    httpClientSpy.post.and.returnValue(of(result));

    service.save(deviceId,'test-tenant').subscribe((success) => {
      expect(success).toEqual(result);
      expect(success.id).toEqual(deviceId);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.post).toHaveBeenCalled();
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
