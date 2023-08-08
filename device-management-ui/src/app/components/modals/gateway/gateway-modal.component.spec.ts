import {ComponentFixture, TestBed} from '@angular/core/testing';
import {GatewayModalComponent} from './gateway-modal.component';
import {NgbActiveModal, NgbPagination} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientModule} from "@angular/common/http";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {NgSelectModule} from "@ng-select/ng-select";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FormsModule} from "@angular/forms";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {SearchFilterPipe} from "../../../shared/search-filter.pipe";
import {SelectDevicesComponent} from "../select-devices/select-devices.component";
import {Device} from "../../../models/device";
import {of} from "rxjs";
import {NotificationService} from "../../../services/notification/notification.service";

describe('GatewayModalComponent', () => {
  let component: GatewayModalComponent;
  let fixture: ComponentFixture<GatewayModalComponent>;
  let activeModalSpy: {
    close: jasmine.Spy
  };
  let notificationServiceSpy = {
    error: jasmine.createSpy('error')
  };

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);

    await TestBed.configureTestingModule({
      imports: [HttpClientModule, OAuthModule.forRoot(), NgSelectModule, FontAwesomeTestingModule, FormsModule, NgbPagination],
      declarations: [GatewayModalComponent, ModalHeadComponent, ModalFooterComponent, SearchFilterPipe, SelectDevicesComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        {provide: NotificationService, useValue: notificationServiceSpy}
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GatewayModalComponent);
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

  it('should close the modal when save is successful after creating a new gateway and updating the via of the bound devices ', () => {
    const gateway = new Device();
    gateway.id = 'test-gateway-id';

    const device = new Device();
    device.id = 'test-device-id';


    component.device = device;
    component.gateway = gateway;
    component.tenantId = 'test-tenant-id';
    component.selectedDevices = [component.device];


    const saveSpy = spyOn(component['deviceService'], 'create').and.returnValue(of(true));
    const updateSpy = spyOn(component['deviceService'], 'update').and.returnValue(of(component.selectedDevices))

    component.onConfirm();

    activeModalSpy.close(component.gateway);

    expect(saveSpy).toHaveBeenCalledWith(component.gateway, component.tenantId);
    expect(updateSpy).toHaveBeenCalledWith(component.selectedDevices[0], component.tenantId);
    expect(activeModalSpy.close).toHaveBeenCalledWith(component.gateway);
  });

  it('should do nothing if the gateway is invalid ', () => {
    const gateway = new Device();

    const device = new Device();
    device.id = 'test-device-id';


    component.device = device;
    component.gateway = gateway;
    component.tenantId = 'test-tenant-id';
    component.selectedDevices = [component.device];


    const saveSpy = spyOn(component['deviceService'], 'create').and.callThrough();
    const updateSpy = spyOn(component['deviceService'], 'update').and.returnValue(of(component.selectedDevices))

    component.onConfirm();

    expect(saveSpy).not.toHaveBeenCalled();
    expect(updateSpy).not.toHaveBeenCalled();
    expect(activeModalSpy.close).not.toHaveBeenCalled();
  });

  it('should return false when all required properties are set', () => {
    const gateway = new Device();
    gateway.id = 'test-gateway-id';

    component.gateway = gateway;
    component.tenantId = 'test-tenant-id';
    component.selectedDevices = [new Device()];

    expect(component.isInvalid()).toBeFalse();
  });

  it('should return true when gateway is missing', () => {
    component.tenantId = 'test-tenant-id';
    component.selectedDevices = [new Device()];

    expect(component.isInvalid()).toBeTrue();
  });

  it('should return true when gatewayId is missing', () => {
    component.gateway = new Device();
    component.tenantId = 'test-tenant-id';
    component.selectedDevices = [new Device()];

    expect(component.isInvalid()).toBeTrue();
  });

  it('should return true when tenantId is missing', () => {
    const gateway = new Device();
    gateway.id = 'test-gateway-id';
    component.gateway = gateway;
    component.selectedDevices = [new Device()];

    expect(component.isInvalid()).toBeTrue();
  });

  it('should return true when selectedDevices is missing', () => {
    const gateway = new Device();
    gateway.id = 'test-gateway-id';
    component.gateway = gateway;
    component.tenantId = 'test-tenant-id';

    expect(component.isInvalid()).toBeTrue();
  });

  it('should return true when selectedDevices.length equals 0', () => {
    const gateway = new Device();
    gateway.id = 'test-gateway-id';
    component.gateway = gateway;
    component.tenantId = 'test-tenant-id';

    component.selectedDevices = [];

    expect(component.isInvalid()).toBeTrue();
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
