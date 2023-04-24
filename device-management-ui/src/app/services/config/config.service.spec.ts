import {TestBed} from '@angular/core/testing';

import {ConfigService} from './config.service';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../api/api.service";
import {of} from "rxjs";
import {Config, ConfigRequest} from "../../models/config";

describe('ConfigService', () => {
  let service: ConfigService;
  let httpClientSpy: {
    post: jasmine.Spy;
    get: jasmine.Spy;
  };
  let apiServiceSpy: {
    getHttpsRequestOptions: jasmine.Spy;
  };

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['post', 'get']);
    apiServiceSpy = jasmine.createSpyObj('ApiService', ['getHttpsRequestOptions']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue: httpClientSpy},
        {provide: ApiService, useValue: apiServiceSpy}
      ]
    });
    service = TestBed.inject(ConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return config when updateConfig is called', () => {
    const request: ConfigRequest = new ConfigRequest();
    httpClientSpy.post.and.returnValue(of(request));

    service.updateConfig('test-device', 'test-tenant', request).subscribe((success) => {
      expect(success).toEqual(request);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.post).toHaveBeenCalled();
  });

  it('should return list of configs when list is called', () => {
    const configs : Config[] = [new Config(), new Config()];
    httpClientSpy.get.and.returnValue(of(configs));

    service.list('test-device', 'test-tenant').subscribe((success) => {
      expect(success).toEqual(configs);
      expect(success.length).toEqual(2);
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.get).toHaveBeenCalled();
  });
});
