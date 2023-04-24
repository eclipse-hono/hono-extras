import {TestBed} from '@angular/core/testing';

import {CommandService} from './command.service';
import {HttpClient} from "@angular/common/http";
import {ApiService} from "../api/api.service";
import {of} from "rxjs";
import {Command} from "../../models/command";

describe('CommandService', () => {
  let service: CommandService;
  let httpClientSpy: {
    post: jasmine.Spy;
  };
  let apiServiceSpy: {
    getHttpsRequestOptions: jasmine.Spy;
  };

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['post']);
    apiServiceSpy = jasmine.createSpyObj('ApiService', ['getHttpsRequestOptions']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue: httpClientSpy},
        {provide: ApiService, useValue: apiServiceSpy}
      ]
    });
    service = TestBed.inject(CommandService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return empty body when sendCommand is called', () => {
    const request: Command = new Command();
    const result = {};
    httpClientSpy.post.and.returnValue(of(result));

    service.sendCommand('test-device', 'test-tenant', request).subscribe((success) => {
      expect(success).toEqual({});
    }, (error) => {
      expect(error).toBeFalsy();
    });
    expect(httpClientSpy.post).toHaveBeenCalled();
  });
});
