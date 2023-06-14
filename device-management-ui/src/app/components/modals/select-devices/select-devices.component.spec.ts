import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceAsGatewayComponent } from './device-as-gateway.component';

describe('DeviceAsGatewayComponent', () => {
  let component: DeviceAsGatewayComponent;
  let fixture: ComponentFixture<DeviceAsGatewayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeviceAsGatewayComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeviceAsGatewayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
