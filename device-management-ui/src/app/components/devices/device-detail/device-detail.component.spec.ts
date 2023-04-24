import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DeviceDetailComponent} from './device-detail.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {ListConfigComponent} from "./list-config/list-config.component";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";
import {DatePipe} from "@angular/common";

describe('DeviceDetailComponent', () => {
  let component: DeviceDetailComponent;
  let fixture: ComponentFixture<DeviceDetailComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, NgbModule, RouterTestingModule],
      declarations: [DeviceDetailComponent, ListConfigComponent],
      providers: [DatePipe]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DeviceDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return the correct device detail', () => {
    component['device'] = {
      id: 'test-id',
      status: {}
    };
    expect(component['deviceDetail']).toEqual('Device: test-id');
  });

  it('should return transformed date when status["created"] is in place', () => {
    const status = {created: '2023-06-14T10:30:00Z'};

    const result = component['getCreationTime'](status);
    expect(result).toEqual('Jun 14, 2023, 10:30:00 AM');
  });

  it('should return "-" when status["created"] is not in place', () => {
    const status = {name: '12345'};

    const result = component['getCreationTime'](status);
    expect(result).toEqual('-');
  });

  it('should navigate back to tenant detail page with state', () => {
    component['tenant'] = {id: 'test-id', ext: 'tenant-test'};

    component['navigateBack']();
    expect(router.navigate).toHaveBeenCalledWith(['tenant-detail', 'test-id'], {
      state: {tenant: component['tenant']},
    });
  });

});
