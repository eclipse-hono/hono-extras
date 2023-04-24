import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DeviceRpkModalComponent} from './device-rpk-modal.component';
import {FormsModule} from "@angular/forms";
import {NgSelectModule} from "@ng-select/ng-select";
import {Secret} from "../../../../models/credentials/secret";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";

describe('DeviceRpkModalComponent', () => {
  let component: DeviceRpkModalComponent;
  let fixture: ComponentFixture<DeviceRpkModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgSelectModule, FormsModule, NgbModule],
      declarations: [DeviceRpkModalComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DeviceRpkModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set rpkSecret to a new Secret if it is not provided', () => {
    component.ngOnInit();
    expect(component.rpkSecret).toEqual(new Secret());
  });

  it('should set notBefore & notAfter to true if rpkSecret has those properties', () => {
    component.rpkSecret = {
      'not-before': '2023-06-01T00:00:00.000Z',
      'not-after': '2023-06-30T23:59:59.999Z'
    };

    component.ngOnInit();

    expect(component['notBefore']).toBeTrue();
    expect(component['notAfter']).toBeTrue();
  });

  it('should set usePublicKey to true and emit an event when called with true', () => {
    spyOn(component.rpkSecretChanged, 'emit');

    component['setUsePublicKey'](true);

    expect(component['usePublicKey']).toBeTrue();
    expect(component.rpkSecret).toEqual(new Secret());
    expect(component.rpkSecretChanged.emit).toHaveBeenCalledWith(undefined);
  });

  it('should set usePublicKey to false and emit an event when called with false', () => {
    spyOn(component.rpkSecretChanged, 'emit');

    component['setUsePublicKey'](false);

    expect(component['usePublicKey']).toBeFalse();
    expect(component.rpkSecret).toEqual(new Secret());
    expect(component.rpkSecretChanged.emit).toHaveBeenCalledWith(undefined);
  });

  it('should emit undefined when rpkSecret is invalid', () => {
    spyOn(component.rpkSecretChanged, 'emit');

    component.rpkSecret = new Secret();

    component['onRpkSecretChanged']();
    expect(component.rpkSecretChanged.emit).toHaveBeenCalledWith(undefined);
  });

  it('should emit rpkSecret when rpkSecret is valid and should delete cert', () => {
    spyOn(component.rpkSecretChanged, 'emit');

    const secret: Secret = new Secret();
    secret.enabled = true;
    secret.algorithm = 'RSA';
    secret.key = 'your-public-key-value';
    secret.cert = 'your-certificate-value';
    secret['not-before'] = '2023-01-01';
    secret['not-after'] = '2024-01-01';

    component.rpkSecret = secret;

    component['onRpkSecretChanged']();

    expect(component.rpkSecretChanged.emit).toHaveBeenCalledWith(secret);
    expect(component.rpkSecret.cert).toBeUndefined();
  });

  it('should emit rpkSecret when rpkSecret is valid and should delete algorithm and key', () => {
    spyOn(component.rpkSecretChanged, 'emit');

    const secret: Secret = new Secret();
    secret.enabled = true;
    secret.algorithm = 'RSA';
    secret.key = 'your-public-key-value';
    secret.cert = 'your-certificate-value';
    secret['not-before'] = '2023-01-01';
    secret['not-after'] = '2024-01-01';

    component.rpkSecret = secret;
    component['usePublicKey'] = false;

    component['onRpkSecretChanged']();

    expect(component.rpkSecretChanged.emit).toHaveBeenCalledWith(secret);
    expect(component.rpkSecret.cert).toEqual('your-certificate-value');
    expect(component.rpkSecret.key).toBeUndefined();
    expect(component.rpkSecret.algorithm).toBeUndefined();
  });

  it('should set "not-before" value to undefined when setNotBeforeDateTime is called with invalid event', () => {
    component['setNotBeforeDateTime'](null);

    expect(component.rpkSecret['not-before']).toBe(undefined);
  });

  it('should set "not-after" value to undefined when setNotBeforeDateTime is called with invalid event', () => {
    component['setNotAfterDateTime'](null);

    expect(component.rpkSecret['not-after']).toBe(undefined);
  });

  it('should return undefined when event data is incomplete ', () => {
    const eventMock = {
      date: {year: 2023, month: 5, day: 14},
      time: {hour: 12, minute: undefined, second: 0}
    };

    const result = component['setDateTime'](eventMock);
    expect(result).toBeUndefined();
  });

  it('should return the correct ISO string when provided with valid event data', () => {
    const eventMock = {
      date: {year: 2023, month: 5, day: 14},
      time: {hour: 12, minute: 30, second: 0}
    };

    const expectedISOString = '2023-06-14T10:30:00.000Z';
    const result = component['setDateTime'](eventMock);
    expect(result).toEqual(expectedISOString);
  });

});
