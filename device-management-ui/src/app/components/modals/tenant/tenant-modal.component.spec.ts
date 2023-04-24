import {ComponentFixture, TestBed} from '@angular/core/testing';
import {TenantModalComponent} from './tenant-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {NgSelectModule} from "@ng-select/ng-select";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";
import {TenantService} from "../../../services/tenant/tenant.service";
import {of, throwError} from "rxjs";
import {NotificationService} from "../../../services/notification/notification.service";

describe('CreateTenantComponent', () => {
  let component: TenantModalComponent;
  let fixture: ComponentFixture<TenantModalComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let tenantServiceSpy: jasmine.SpyObj<TenantService>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);
    tenantServiceSpy = jasmine.createSpyObj('TenantService', ['create', 'update']);
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['error']);
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), NgSelectModule, FontAwesomeTestingModule, FormsModule],
      declarations: [TenantModalComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        {provide: TenantService, useValue: tenantServiceSpy},
        {provide: NotificationService, useValue: notificationServiceSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TenantModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close the active modal', () => {
    component['onClose']();
    expect(activeModalSpy.close).toHaveBeenCalled();
  });

  it('should set modal title to create Tenant if isNewTenant is true', () => {
    component.isNewTenant = true;
    component.ngOnInit();
    expect(component['modalTitle']).toEqual('Create Tenant');
    expect(component.tenant.ext).toEqual({'messaging-type': ''});
  });

  it('should set modal title to Edit Tenant if isNewTenant is false', () => {
    component.tenant = {
      id: 'tenant-id',
      ext: {'messaging-type': 'pubsub'}
    };
    component.isNewTenant = false;
    component.ngOnInit();
    expect(component['modalTitle']).toEqual('Edit Tenant');
    expect(component.tenant.ext).toEqual({'messaging-type': 'pubsub'});
  });

  it('should call createTenant when isNewTenant is true', () => {
    component.isNewTenant = true;
    component.tenant = {
      id: 'test-id',
      ext: {'messaging-type': 'pubsub'}
    };

    tenantServiceSpy.create.and.returnValue(of(true));

    component['onConfirm']();

    expect(tenantServiceSpy.create).toHaveBeenCalledWith(component.tenant);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.tenant);
    expect(tenantServiceSpy.update).not.toHaveBeenCalledWith(component.tenant);
  });

  it('should call updateTenant when isNewTenant is false', () => {
    component.isNewTenant = false;
    component.tenant = {
      id: 'test-id',
      ext: {'messaging-type': 'pubsub'}
    };

    tenantServiceSpy.update.and.returnValue(of(true));

    component['onConfirm']();

    expect(tenantServiceSpy.create).not.toHaveBeenCalledWith(component.tenant);
    expect(tenantServiceSpy.update).toHaveBeenCalledWith(component.tenant);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.tenant);
  });

  it('should call createTenant and give an error notification', () => {
    component.isNewTenant = true;
    component.tenant = {
      id: 'tenant-id',
      ext: {'messaging-type': 'pubsub'}
    };

    tenantServiceSpy.create.and.returnValue(throwError(new Error('Create failed')));

    component['onConfirm']();

    expect(notificationServiceSpy.error).toHaveBeenCalledWith('Could not create tenant');
  });

  it('should call updateTenant and give an error notification', () => {
    component.isNewTenant = false;
    component.tenant = {
      id: 'tenant-id',
      ext: {'messaging-type': 'pubsub'}
    };

    tenantServiceSpy.update.and.returnValue(throwError(new Error('Update failed')));

    component['onConfirm']();

    expect(notificationServiceSpy.error).toHaveBeenCalledWith('Could not update tenant');
  });

  it('should not call any methods when tenant data is invalid', () => {
    component.tenant = {
      id: '',
      ext: {'messaging-type': ''}
    };

    component['onConfirm']();

    expect(tenantServiceSpy.create).not.toHaveBeenCalledWith(component.tenant);
    expect(tenantServiceSpy.update).not.toHaveBeenCalledWith(component.tenant);
  });

});
