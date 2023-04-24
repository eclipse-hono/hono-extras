import {ComponentFixture, TestBed} from '@angular/core/testing';
import {TenantDetailComponent} from './tenant-detail.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {ListConfigComponent} from "../../devices/device-detail/list-config/list-config.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {DeviceListComponent} from "../../devices/device-list/device-list.component";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";

describe('TenantDetailComponent', () => {
  let component: TenantDetailComponent;
  let fixture: ComponentFixture<TenantDetailComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, NgbModule, RouterTestingModule],
      declarations: [TenantDetailComponent, DeviceListComponent, ListConfigComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TenantDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return the correct tenant detail', () => {
    component['tenant'] = {
      id: 'test-tenant',
      ext: {}
    };
    expect(component['tenantDetail']).toEqual('Tenant: test-tenant');
  });

  it('should return the messaging-type value if it is defined in the tenant extension', () => {
    component['tenant'].ext = {
      'messaging-type': 'pubsub'
    };
    expect(component['messagingType']).toEqual('pubsub');
  });

  it('should return "-" if the messaging-type property is not defined in the tenant extension', () => {
    expect(component['messagingType']).toEqual('-');
  });

  it('should navigate back to tenant list', () => {
    component['navigateBack']();
    expect(router.navigate).toHaveBeenCalledWith(['tenant-list']);
  });

});
