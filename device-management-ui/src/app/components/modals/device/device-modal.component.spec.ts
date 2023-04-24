import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DeviceModalComponent} from './device-modal.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";
import {DeviceService} from "../../../services/device/device.service";
import {of, throwError} from "rxjs";
import {NotificationService} from "../../../services/notification/notification.service";
import {Device} from "../../../models/device";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

describe('CreateDeviceComponent', () => {
  let component: DeviceModalComponent;
  let fixture: ComponentFixture<DeviceModalComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let deviceServiceSpy: jasmine.SpyObj<DeviceService>;
  const notificationServiceSpy = {
    error: jasmine.createSpy('error')
  };

  beforeEach(async () => {
    deviceServiceSpy = jasmine.createSpyObj('DeviceService', ['save']);
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, FormsModule],
      declarations: [DeviceModalComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        {provide: DeviceService, useValue: deviceServiceSpy},
        {provide: NotificationService, useValue: notificationServiceSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DeviceModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close the active modal on cancel', () => {
    component['onClose']();
    expect(activeModalSpy.close).toHaveBeenCalled();
  });

  it('should close the modal when save is successful', () => {
    const device = new Device();
    device.id = 'test-device-id';

    component.device = device;
    component.tenantId = 'test-tenant-id';

    deviceServiceSpy.save.and.returnValue(of(true));

    component['onConfirm']();
    expect(deviceServiceSpy.save).toHaveBeenCalledWith(device.id, component.tenantId);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.device);
  });

  it('should show error notification when save fails', () => {
    const device = new Device();
    device.id = 'test-device-id';

    component.device = device;
    component.tenantId = 'test-tenant-id';

    deviceServiceSpy.save.and.returnValue(throwError(new Error('test error')));

    component['onConfirm']();

    expect(deviceServiceSpy.save).toHaveBeenCalledWith('test-device-id', 'test-tenant-id');
    expect(notificationServiceSpy.error).toHaveBeenCalledWith('Could not create device for id <strong>test-device-id</strong>');
  });

});
