import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DevicePasswordModalComponent} from './device-password-modal.component';
import {FormsModule} from "@angular/forms";
import {Secret} from "../../../../models/credentials/secret";

describe('DevicePasswordModalComponent', () => {
  let component: DevicePasswordModalComponent;
  let fixture: ComponentFixture<DevicePasswordModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [DevicePasswordModalComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DevicePasswordModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set usePlainPassword to true and emit an event', () => {
    spyOn(component.passwordSecretChanged, 'emit');

    component['setUsePlainPassword'](true);

    expect(component['usePlainPassword']).toBeTrue();
    expect(component['passwordSecret']).toEqual(new Secret());
    expect(component.passwordSecretChanged.emit).toHaveBeenCalledWith(undefined);
  });

  it('should set usePlainPassword to false and emit an event', () => {
    spyOn(component.passwordSecretChanged, 'emit');

    component['setUsePlainPassword'](false);

    expect(component['usePlainPassword']).toBeFalse();
    expect(component['passwordSecret']).toEqual(new Secret());
    expect(component.passwordSecretChanged.emit).toHaveBeenCalledWith(undefined);
  });

  it('should emit undefined when passwordSecret is invalid', () => {
    spyOn(component.passwordSecretChanged, 'emit');

    component['onPasswordSecretChanged']();
    expect(component.passwordSecretChanged.emit).toHaveBeenCalledWith(undefined);
  });

  it('should emit passwordSecret when passwordSecret is valid and usePlainPassword is true, should remove pwd-hash, hash-function and salt', () => {
    const secret = new Secret();
    secret['pwd-plain'] = 'password';
    secret['pwd-hash'] = 'hash';
    secret['hash-function'] = 'sha256';
    secret['salt'] = 'salt';

    spyOn(component.passwordSecretChanged, 'emit');

    component['usePlainPassword'] = true;
    component['passwordSecret'] = secret;

    component['onPasswordSecretChanged']();

    expect(component.passwordSecretChanged.emit).toHaveBeenCalledWith(secret);
    expect(component['passwordSecret']['pwd-hash']).toBeUndefined();
    expect(component['passwordSecret']['hash-function']).toBeUndefined();
    expect(component['passwordSecret']['salt']).toBeUndefined();
  });

  it('should emit passwordSecret when passwordSecret is valid and usePlainPassword is false, should remove pwd-plain', () => {
    const secret = new Secret();
    secret['pwd-hash'] = 'hash';
    secret['hash-function'] = 'sha256';
    secret['pwd-plain'] = 'password';

    spyOn(component.passwordSecretChanged, 'emit');

    component['usePlainPassword'] = false;
    component['passwordSecret'] = secret;

    component['onPasswordSecretChanged']();

    expect(component.passwordSecretChanged.emit).toHaveBeenCalledWith(secret);
    expect(component['passwordSecret']['pwd-plain']).toBeUndefined();
  });

});
