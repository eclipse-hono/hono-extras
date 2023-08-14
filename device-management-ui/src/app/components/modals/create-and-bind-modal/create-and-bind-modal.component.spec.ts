import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAndBindModalComponent } from './create-and-bind-modal.component';
import {NgbActiveModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {SelectDevicesComponent} from "../select-devices/select-devices.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {Device} from "../../../models/device";
import {of} from "rxjs";
import {DeviceService} from "../../../services/device/device.service";
import {FormsModule} from "@angular/forms";

describe('CreateAndBindModalComponent', () => {
  let component: CreateAndBindModalComponent;
  let fixture: ComponentFixture<CreateAndBindModalComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let deviceServiceSpy: {
    save: jasmine.Spy,
    listByTenant: jasmine.Spy
  };

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);

    await TestBed.configureTestingModule({
      imports: [NgbModule, HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, FormsModule],
      declarations: [ CreateAndBindModalComponent, ModalHeadComponent, ModalFooterComponent, SelectDevicesComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy}
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    deviceServiceSpy = jasmine.createSpyObj('DeviceService', ['create', 'listByTenant']);
    fixture = TestBed.createComponent(CreateAndBindModalComponent);
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

  it('should return false when isDeviceFlag and all required properties are set ', () => {
    const device = new Device();
    component.isDeviceFlag = true;
    device.id = 'test-device-id';

    component.device = device;
    component.tenantId = 'test-tenant-id'
    component.sendViaGateway = false;

    expect(component.isInvalid()).toBeFalse();
  });

  it('should return true when isDeviceFlag and required properties are not set ', () => {
    const device = new Device();
    component.isDeviceFlag = true;
    device.id = 'test-device-id';

    component.device = device;
    component.sendViaGateway = false;

    expect(component.isInvalid()).toBeTruthy();
  });

  it('should call createDevice when isDeviceFlag is true', () => {
    component.isDeviceFlag = true;

    const createDeviceSpy = spyOn(component as any, 'createDevice').and.callThrough();
    const createGatewaySpy = spyOn(component as any, 'createGateway').and.callThrough();
    const bindDeviceSpy = spyOn(component as any, 'bindDevice').and.callThrough();
    spyOn(component, 'isInvalid').and.returnValue(false)

    component.onConfirm();

    expect(createDeviceSpy).toHaveBeenCalled();
    expect(createGatewaySpy).not.toHaveBeenCalled();
    expect(bindDeviceSpy).not.toHaveBeenCalled();
  });

  it('should call createGateway when isGatewayFlag is true', () => {
    component.isGatewayFlag = true;

    const createDeviceSpy = spyOn(component as any, 'createDevice').and.callThrough();
    const createGatewaySpy = spyOn(component as any, 'createGateway').and.callThrough();
    const bindDeviceSpy = spyOn(component as any, 'bindDevice').and.callThrough();
    spyOn(component, 'isInvalid').and.returnValue(false)

    component.onConfirm();

    expect(createDeviceSpy).not.toHaveBeenCalled();
    expect(createGatewaySpy).toHaveBeenCalled();
    expect(bindDeviceSpy).not.toHaveBeenCalled();
  });

  it('should call bindDevice when isBindDeviceFlag is true', () => {
    component.isBindDeviceFlag = true;

    const createDeviceSpy = spyOn(component as any, 'createDevice').and.callThrough();
    const createGatewaySpy = spyOn(component as any, 'createGateway').and.callThrough();
    const bindDeviceSpy = spyOn(component as any, 'bindDevice').and.callThrough();
    spyOn(component, 'isInvalid').and.returnValue(false)

    component.onConfirm();

    expect(createDeviceSpy).not.toHaveBeenCalled();
    expect(createGatewaySpy).not.toHaveBeenCalled();
    expect(bindDeviceSpy).toHaveBeenCalled();
  });

  it('should create a device when onConfirm is called and should close modal after save', () => {
    const device = new Device();
    device.id = 'test-device-id';

    const gateway = new Device();
    gateway.id = 'test-gateway-id';

    component.selectedDevices = [gateway];
    component.device = device;
    component.tenantId = 'test-tenant-id';
    component.isDeviceFlag = true;
    component.sendViaGateway = true;

    const saveSpy = spyOn(component['deviceService'], 'create').and.returnValue(of(true));

    component.onConfirm();
    expect(component.device.via).toHaveSize(1);
    expect(saveSpy).toHaveBeenCalledWith(component.device, component.tenantId);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.device);
  });

  it('should create a gateway when onConfirm is called and should close modal after save', () => {
    const gateway = new Device();
    gateway.id = 'test-device-id';

    component.device = gateway;
    component.tenantId = 'test-tenant-id';
    component.isGatewayFlag = true;
    component.selectedDevices = [new Device()];

    const saveSpy = spyOn(component['deviceService'], 'create').and.returnValue(of(true));
    const updateSpy = spyOn(component["deviceService"], 'update').and.returnValue(of(component.selectedDevices));

    component.onConfirm();
    expect(saveSpy).toHaveBeenCalledWith(component.device, component.tenantId);
    expect(updateSpy).toHaveBeenCalledWith(component.selectedDevices[0], component.tenantId);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.device);
  });

  it('should bind new device(s) when onConfirm is called and should close modal after save', () => {
    const device1 = new Device();
    device1.id = 'device1';

    const device2 = new Device();
    device2.id = 'device2';

    const gateway = new Device();
    gateway.id = 'gateway1';

    component.device = gateway;
    component.tenantId = 'test-tenant-id';
    component.selectedDevices = [device1, device2];
    component.isBindDeviceFlag = true;

    const updateSpy = spyOn(component["deviceService"], 'update').and.returnValue(of());

    component.onConfirm();
    expect(updateSpy).toHaveBeenCalledTimes(2);
    expect(updateSpy).toHaveBeenCalledWith(device1, 'test-tenant-id');
    expect(updateSpy).toHaveBeenCalledWith(device2, 'test-tenant-id');
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.device);
  });

  it('should do nothing when device is invalid', () => {
    const device = new Device();
    device.id = 'test-device-id';

    component.device = device;
    component.tenantId = 'test-tenant-id';
    component.sendViaGateway = true;
    component.isDeviceFlag = true;

    const saveSpy = spyOn(component['deviceService'], 'create').and.callThrough();

    component.onConfirm();
    expect(saveSpy).not.toHaveBeenCalled();
    expect(activeModalSpy.close).not.toHaveBeenCalled();
  });

  it('should set pageOffset and call listAll function', () => {
    component['pageOffset'] = 2;
    component['pageSize'] = 10;
    component.tenantId = 'test-tenant-id';

    const listResult = {
      result: [new Device(), new Device()],
      total: 12
    };
    const listSpy = spyOn(component['deviceService'], 'listByTenant').and.returnValue(of(listResult));

    component['onPageOffsetChanged'](3);

    expect(listSpy).toHaveBeenCalledWith('test-tenant-id', 10, 3, false);
    expect(component['pageOffset']).toEqual(3);
    expect(component.devices.length).toEqual(2);
  });
});
