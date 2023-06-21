import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BindDevicesModalComponent } from './bind-devices-modal.component';
import {NgbActiveModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {SelectDevicesComponent} from "../select-devices/select-devices.component";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";
import {DeviceService} from "../../../services/device/device.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {Device} from "../../../models/device";
import {of} from "rxjs";

describe('BindDevicesModalComponent', () => {
  let component: BindDevicesModalComponent;
  let fixture: ComponentFixture<BindDevicesModalComponent>;
  let deviceService: jasmine.SpyObj<DeviceService>;
  let notificationService: NotificationService;
  let activeModalSpy: {
    close: jasmine.Spy;
  };
  let deviceServiceSpy: {
    listByTenant: jasmine.Spy;
    update: jasmine.Spy;
  };

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('DeviceService', ['close'])
    deviceServiceSpy = jasmine.createSpyObj('DeviceService', ['listByTenant', 'update'])
;    await TestBed.configureTestingModule({
      imports: [NgbModule, HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, FormsModule],
      declarations: [ BindDevicesModalComponent, ModalHeadComponent, SelectDevicesComponent, ModalFooterComponent],
      providers: [
        { provide: DeviceService, useValue: deviceServiceSpy },
        { provide: NotificationService, useValue: notificationService },
        { provide: NgbActiveModal, useValue: activeModalSpy }
      ]
    })
    .compileComponents();
    fixture = TestBed.createComponent(BindDevicesModalComponent);
    component = fixture.componentInstance;
    deviceService = TestBed.inject(DeviceService) as jasmine.SpyObj<DeviceService>;
    notificationService = TestBed.inject(NotificationService);
    activeModalSpy = TestBed.inject(NgbActiveModal) as jasmine.SpyObj<NgbActiveModal>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should bind devices and emit selected devices', () => {
    const device1: Device = new Device();
    device1.id = 'device1';
    const device2: Device = new Device();
    device2.id = 'device2';
    component.deviceId = 'gateway1';
    component.tenantId = 'tenant1';
    component.selectedDevices = [device1, device2];

    deviceServiceSpy.listByTenant.and.returnValue(of({result: [device1, device2]}));
    deviceServiceSpy.update.and.returnValue(of({}));
    activeModalSpy.close.and.returnValue(of(null));


    component.onConfirm();

    expect(deviceService.update).toHaveBeenCalledTimes(2);
    expect(activeModalSpy.close).toHaveBeenCalled();

    expect(deviceService.update).toHaveBeenCalledWith(device1, 'tenant1');
    expect(deviceService.update).toHaveBeenCalledWith(device2, 'tenant1');
  });

});
