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
import {SelectDevicesComponent} from "../select-devices/select-devices.component";

describe('DeviceModalComponent', () => {
  let component: DeviceModalComponent;
  let fixture: ComponentFixture<DeviceModalComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let deviceServiceSpy: {
    save: jasmine.Spy,
    listAll: jasmine.Spy
  };
  const notificationServiceSpy = {
    error: jasmine.createSpy('error')
  };

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, FormsModule],
      declarations: [DeviceModalComponent, ModalHeadComponent, ModalFooterComponent, SelectDevicesComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        {provide: NotificationService, useValue: notificationServiceSpy}
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    deviceServiceSpy = jasmine.createSpyObj('DeviceService', ['create', 'listAll']);
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

    const saveSpy = spyOn(component['deviceService'], 'create').and.returnValue(of(true));

    component.onConfirm();
    expect(saveSpy).toHaveBeenCalledWith(device, component.tenantId);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.device);
  });

  it('should do nothing when device is invalid', () => {
    const device = new Device();
    device.id = 'test-device-id';

    component.device = device;
    component.tenantId = 'test-tenant-id';
    component['sendViaGateway'] = true;

    const saveSpy = spyOn(component['deviceService'], 'create').and.callThrough();

    component.onConfirm();
    expect(saveSpy).not.toHaveBeenCalled();
    expect(activeModalSpy.close).not.toHaveBeenCalled();
  });

  it('should show error notification when save fails', () => {
    const device = new Device();
    device.id = 'test-device-id';

    component.device = device;
    component.tenantId = 'test-tenant-id';

    const saveSpy = spyOn(component['deviceService'], 'create').and.returnValue(throwError(new Error('test error')));

    component.onConfirm();

    expect(saveSpy).toHaveBeenCalledWith(device, 'test-tenant-id');
    expect(notificationServiceSpy.error).toHaveBeenCalledWith('Could not create device for id <strong>test-device-id</strong>');
  });

  it('should set pageOffset and call listAll function', () => {
    component['pageOffset'] = 2;
    component['pageSize'] = 10;
    component.tenantId = 'test-tenant-id';

    const listResult = {
      result: [new Device(), new Device()],
      total: 12
    };
    const listSpy = spyOn(component['deviceService'], 'listAll').and.returnValue(of(listResult));

    component['onPageOffsetChanged'](3);

    expect(listSpy).toHaveBeenCalledWith('test-tenant-id', 10, 3);
    expect(component['pageOffset']).toEqual(3);
    expect(component.devices.length).toEqual(2);
  });

});
