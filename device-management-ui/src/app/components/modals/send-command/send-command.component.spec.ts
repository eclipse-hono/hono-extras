import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SendCommandComponent} from './send-command.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";
import {Command} from "../../../models/command";
import {CommandService} from "../../../services/command/command.service";
import {of, throwError} from "rxjs";
import {NotificationService} from "../../../services/notification/notification.service";

describe('SendCommandComponent', () => {
  let component: SendCommandComponent;
  let fixture: ComponentFixture<SendCommandComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;
  let commandService: CommandService;
  let notificationService: NotificationService;

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);
    notificationService = jasmine.createSpyObj('NotificationService', ['error']);
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot(), FontAwesomeTestingModule, FormsModule],
      declarations: [SendCommandComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy},
        CommandService,
        {provide: NotificationService, useValue: notificationService},
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SendCommandComponent);
    component = fixture.componentInstance;
    commandService = TestBed.inject(CommandService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set useText to true when event target value is "text"', () => {
    const event = {target: {value: 'text'}};
    component['onChange'](event);

    expect(component['useText']).toBeTrue();
  });

  it('should set useText to false when event target value is not "text"', () => {
    const event = {target: {value: 'otherValue'}};
    component['onChange'](event);

    expect(component['useText']).toBeFalse();
  });

  it('should not do anything when any required property is missing', () => {
    component['onConfirm']();

    expect(notificationService.error).not.toHaveBeenCalled();
    expect(activeModalSpy.close).not.toHaveBeenCalled();
  });

  it('should send command and close active modal', () => {
    const command = new Command();
    command.binaryData = 'testBinaryData';

    component.deviceId = 'device-id';
    component.tenantId = 'tenant-id';
    component['command'] = command;

    spyOn(commandService, 'sendCommand').and.returnValue(of({}));
    component['onConfirm']();

    expect(commandService.sendCommand).toHaveBeenCalledWith('device-id', 'tenant-id', command);
    expect(activeModalSpy.close).toHaveBeenCalledWith(command);
  });

  it('should throw error on sendCommand and show notification', () => {
    const command = new Command();
    command.binaryData = 'testBinaryData';

    component.deviceId = 'device-id';
    component.tenantId = 'tenant-id';
    component['command'] = command;

    spyOn(commandService, 'sendCommand').and.returnValue(throwError({error: {error: 'error'}, message: 'error message'}));
    component['onConfirm']();

    expect(commandService.sendCommand).toHaveBeenCalledWith('device-id', 'tenant-id', command);
    expect(activeModalSpy.close).not.toHaveBeenCalled();
    expect(notificationService.error).toHaveBeenCalledWith('Could not send command to device <strong>device-id</strong>. Reason: error');
  });

  it('should close the active modal', () => {
    component['onClose']();
    expect(activeModalSpy.close).toHaveBeenCalled();
  });

});
