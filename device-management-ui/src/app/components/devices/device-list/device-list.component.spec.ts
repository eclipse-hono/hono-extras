import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DeviceListComponent} from './device-list.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";

describe('DeviceListComponent', () => {
  let component: DeviceListComponent;
  let fixture: ComponentFixture<DeviceListComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, NgbModule, RouterTestingModule],
      declarations: [DeviceListComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DeviceListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return creationTime', () => {
    const status = {created: '12345'};

    const result = component['getCreationTime'](status);
    expect(result).toEqual('12345');
  });

  it('should return "-"', () => {
    const status = {name: '12345'};

    const result = component['getCreationTime'](status);
    expect(result).toEqual('-');
  });

  it('should navigate to device detail on selectDevice', () => {
    const device = new Device();
    device.id = 'device-id';
    const tenant = new Tenant();
    tenant.id = 'tenant-id';

    component.tenant = tenant;

    component['selectDevice'](device);

    expect(router.navigate).toHaveBeenCalledWith(
      ['device-detail', 'device-id'],
      {state: {tenant, device}}
    );
  });

});
