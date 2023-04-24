import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CredentialsModalComponent} from './credentials-modal.component';
import {NgbActiveModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {NgSelectModule} from "@ng-select/ng-select";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";
import {Credentials, CredentialTypes} from "../../../models/credentials/credentials";
import {Secret} from "../../../models/credentials/secret";
import {CredentialsService} from "../../../services/credentials/credentials.service";
import {of, throwError} from "rxjs";
import {DeviceRpkModalComponent} from "./device-rpk-modal/device-rpk-modal.component";

describe('CredentialsModalComponent', () => {
  let component: CredentialsModalComponent;
  let fixture: ComponentFixture<CredentialsModalComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let credentialsServiceSpy: CredentialsService;
  let deviceRpkModalComponentSpy: jasmine.SpyObj<DeviceRpkModalComponent>;

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);
    deviceRpkModalComponentSpy = jasmine.createSpyObj('DeviceRpkModalComponent', ['setNotBeforeDateTime']);
    await TestBed.configureTestingModule({
      imports: [NgbModule, HttpClientTestingModule, OAuthModule.forRoot(), NgSelectModule, FontAwesomeTestingModule, FormsModule],
      declarations: [CredentialsModalComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        {provide: CredentialsService},
        {provide: DeviceRpkModalComponent, useValue: deviceRpkModalComponentSpy}
      ]
    })
      .compileComponents();

    credentialsServiceSpy = TestBed.inject(CredentialsService);
    fixture = TestBed.createComponent(CredentialsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set modalTitle to "Add Credentials" when isNewCredentials is true', () => {
    component.isNewCredentials = true;

    component.ngOnInit();
    expect(component['modalTitle']).toEqual('Add Credentials');
  });

  it('should set modalTitle to "Update Credentials" and authId and authType when isNewCredentials is false and credential is provided', () => {
    component.isNewCredentials = false;
    component.credential = {
      'auth-id': 'auth123',
      type: 'hashed-password',
      secrets: []
    };

    component.ngOnInit();
    expect(component['modalTitle']).toEqual('Update Credentials');
    expect(component['authId']).toEqual('auth123');
    expect(component['authType']).toEqual('hashed-password');
  });

  it('should return true when deviceId is not set', () => {
    component.tenantId = 'testTenantId';
    expect(component['isInvalid']()).toBeTrue();
  });

  it('should return true when tenantId is not set', () => {
    component.deviceId = 'testDeviceId';
    expect(component['isInvalid']()).toBeTrue();
  });

  it('should return true when authentication is not valid', () => {
    component.deviceId = 'testDeviceId';
    component.tenantId = 'testTenantId';
    component.credential = new Credentials();
    component.credential.secrets = [new Secret()];
    component['authId'] = '';
    component['authType'] = '';

    expect(component['isInvalid']()).toBeTrue();
  });

  it('should return true when secret is not available', () => {
    component.deviceId = 'testDeviceId';
    component.tenantId = 'testTenantId';
    component.credential = new Credentials();
    component.credential.secrets = [];
    component['authId'] = 'testAuthId';
    component['authType'] = 'testAuthType';

    expect(component['isInvalid']()).toBeTrue();
  });

  it('should return false when values are valid', () => {
    component.deviceId = 'testDeviceId';
    component.tenantId = 'testTenantId';
    component.credential = new Credentials();
    component.credential.secrets = [new Secret()];
    component['authId'] = 'testAuthId';
    component['authType'] = 'testAuthType';

    expect(component['isInvalid']()).toBeFalse();
  });

  it('should close the modal', () => {
    component['onClose']();
    expect(activeModalSpy.close).toHaveBeenCalled();
  });

  it('should call the credentials service save method when credentials are valid', () => {
    spyOn(credentialsServiceSpy, 'save').and.returnValue(of({}));

    const secret: Secret = new Secret();
    secret.enabled = true;
    secret.algorithm = 'RSA';
    secret.key = 'your-public-key-value';
    secret['not-before'] = '2023-01-01';
    secret['not-after'] = '2024-01-01';

    component.deviceId = 'test-device-id';
    component.tenantId = 'test-tenant-id';
    component.credential.secrets = [secret];
    component['authId'] = 'test-auth-id';
    component['authType'] = CredentialTypes.HASHED_PASSWORD;

    component['onConfirm']();
    expect(credentialsServiceSpy.save).toHaveBeenCalledWith(
      component.deviceId,
      component.tenantId,
      component.credentials
    );
  });

  it('should add a new credentials', () => {
    spyOn(credentialsServiceSpy, 'save').and.returnValue(of({}));

    component.deviceId = 'test-device-id';
    component.tenantId = 'test-tenant-id';
    component.credential.secrets = [new Secret()];
    component['authId'] = 'test-auth-id';
    component['authType'] = CredentialTypes.HASHED_PASSWORD;

    component['onConfirm']();
    expect(component.credentials.length).toEqual(1);
  });

  it('should remove the new credential when there is an error adding it', () => {
    spyOn(credentialsServiceSpy, 'save').and.returnValue(
      throwError('error adding credentials')
    );
    component.deviceId = 'test-device-id';
    component.tenantId = 'test-tenant-id';
    component.credential.secrets = [new Secret()];
    component['authId'] = 'test-auth-id';
    component['authType'] = CredentialTypes.HASHED_PASSWORD;
    component.isNewCredentials = true;

    component['onConfirm']();
    expect(component.credentials.length).toEqual(0);
  });

  it('should not call the credentials save method when the credentials are not valid', () => {
    spyOn(credentialsServiceSpy, 'save').and.returnValue(of({}));

    component['onConfirm']();
    expect(credentialsServiceSpy.save).not.toHaveBeenCalled();
  });

});
