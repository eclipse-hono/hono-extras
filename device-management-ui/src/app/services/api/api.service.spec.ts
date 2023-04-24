import {TestBed} from '@angular/core/testing';

import {ApiService} from './api.service';
import {GoogleService} from "../google/google.service";

describe('ApiService', () => {
  let service: ApiService;
  let googleServiceSpy: {
    getIdToken: jasmine.Spy;
  }

  beforeEach(() => {
    googleServiceSpy = jasmine.createSpyObj('GoogleService', ['getIdToken']);
    TestBed.configureTestingModule({
      providers: [
        {provide: GoogleService, useValue: googleServiceSpy}
      ]
    });
    service = TestBed.inject(ApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
