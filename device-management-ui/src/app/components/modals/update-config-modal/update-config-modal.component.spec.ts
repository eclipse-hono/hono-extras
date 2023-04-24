import {ComponentFixture, TestBed} from '@angular/core/testing';
import {UpdateConfigModalComponent} from './update-config-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";
import {ConfigService} from "../../../services/config/config.service";
import {Config, ConfigRequest} from "../../../models/config";
import {of} from "rxjs";
import {NotificationService} from "../../../services/notification/notification.service";

describe('UpdateConfigModalComponent', () => {
  let component: UpdateConfigModalComponent;
  let fixture: ComponentFixture<UpdateConfigModalComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let mockConfigService: jasmine.SpyObj<ConfigService>;
  let mockNotificationService: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);
    mockConfigService = jasmine.createSpyObj('ConfigService', ['updateConfig']);
    mockNotificationService = jasmine.createSpyObj('NotificationService', ['error']);
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, FormsModule],
      declarations: [UpdateConfigModalComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        {provide: ConfigService, useValue: mockConfigService},
        {provide: NotificationService, useValue: mockNotificationService}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(UpdateConfigModalComponent);
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

  it('should return false when event has not property value "text"', () => {
    const event = {target: {val: 'not value'}};
    component['onChange'](event);
    expect(component['useText']).toBeFalse();
  });

  it('should return true when event has property value "text"', () => {
    const event = {target: {value: 'text'}};
    component['onChange'](event);
    expect(component['useText']).toBeTrue();
  });

  it('should not do anything when tenantId is not defined', () => {
    component.deviceId = 'deviceID';
    component['config'] = {
      binaryData: 'test data'
    };

    const updateConfigSpy = mockConfigService.updateConfig.and.callThrough();

    component['onConfirm']();

    expect(updateConfigSpy).not.toHaveBeenCalled();
  });

  it('should update the config and close the modal with the result', () => {
    const mockConfig: ConfigRequest = {
      binaryData: 'test data'
    };

    const expectedUpdatedConfig: Config = {
      version: '2',
      cloudUpdateTime: 'today',
      binaryData: 'test data',
      deviceAckTime: ''
    };

    component['config'] = mockConfig;
    component.deviceId = 'deviceID';
    component.tenantId = 'tenantID';

    const updateConfigSpy = mockConfigService.updateConfig.and.returnValue(of(expectedUpdatedConfig));

    component['onConfirm']();

    expect(updateConfigSpy).toHaveBeenCalledWith(component.deviceId, component.tenantId, mockConfig);
    expect(component['config'].binaryData).toEqual('dGVzdCBkYXRh');
    expect(activeModalSpy.close).toHaveBeenCalledWith(expectedUpdatedConfig);
    expect(mockNotificationService.error).not.toHaveBeenCalled();
  });

});
