import {ComponentFixture, TestBed} from '@angular/core/testing';
import {TenantListComponent} from './tenant-list.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {Router} from "@angular/router";
import {RouterTestingModule} from "@angular/router/testing";
import {Tenant} from "../../../models/tenant";
import {TenantService} from "../../../services/tenant/tenant.service";
import {of} from "rxjs";

describe('TenantListComponent', () => {
  let component: TenantListComponent;
  let fixture: ComponentFixture<TenantListComponent>;
  let tenantServiceSpy: {
    list: jasmine.Spy,
    delete: jasmine.Spy
  };
  const routerSpy = {navigate: jasmine.createSpy('navigate')};

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, NgbModule, RouterTestingModule],
      declarations: [TenantListComponent],
      providers: [
        {provide: NgbModule, useValue: {}},
        {provide: Router, useValue: routerSpy},]
    })
      .compileComponents();
  });

  beforeEach(() => {
    tenantServiceSpy = jasmine.createSpyObj('TenantService', ['list', 'delete']);
    fixture = TestBed.createComponent(TenantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return messagingType', () => {
    const ext = {'messaging-type': 'pubsub'};
    const result = component['getMessagingType'](ext);
    expect(result).toEqual('pubsub');
  });

  it('should return "-"', () => {
    const ext = {name: 'pubsub'};
    const result = component['getMessagingType'](ext);
    expect(result).toEqual('-');
  });

  it('should navigate back to tenant detail page with state', () => {
    const tenant: Tenant = {id: 'test-id', ext: 'tenant-test'};
    component['selectTenant'](tenant);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['tenant-detail', 'test-id'], {
      state: {tenant: tenant},
    });
  });

  it('should set pageOffset and call list method', () => {
    const tenantsResponse = {
      result: [{id: 'tenant'}, {id: 'tenant2'}],
      total: 150
    };
    const listSpy = spyOn(component['tenantService'], 'list').and.returnValue(of(tenantsResponse));

    component['pageSize'] = 100;
    component['changePage'](3);

    expect(listSpy).toHaveBeenCalledWith(100, 200);
    expect(component['pageOffset']).toEqual(200);
  });

  it('should set pageSize and call list method', () => {
    const tenantsResponse = {
      result: [{id: 'tenant'}, {id: 'tenant2'}],
      total: 150
    };
    const listSpy = spyOn(component['tenantService'], 'list').and.returnValue(of(tenantsResponse));

    component['pageOffset'] = 0;
    component['setPageSize'](100);

    expect(listSpy).toHaveBeenCalledWith(100, 0);
    expect(component['pageSize']).toEqual(100);
  });

});
