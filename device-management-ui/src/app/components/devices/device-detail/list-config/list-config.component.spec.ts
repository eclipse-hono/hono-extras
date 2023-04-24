import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ListConfigComponent} from './list-config.component';
import {Config} from "../../../../models/config";
import {faCircleCheck, faTriangleExclamation} from "@fortawesome/free-solid-svg-icons";

describe('ListConfigComponent', () => {
  let component: ListConfigComponent;
  let fixture: ComponentFixture<ListConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListConfigComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return circleCheck icon when deviceAckTime is defined', () => {
    const config: Config = new Config();
    config.deviceAckTime = '123';
    expect(component.getDeviceAckTime(config)).toEqual(faCircleCheck)
  });

  it('should return triangleExclamation icon when deviceAckTime is not defined', () => {
    const config: Config = new Config();
    expect(component.getDeviceAckTime(config)).toEqual(faTriangleExclamation)
  });

  it('should return green icon color when deviceAckTime is defined', () => {
    const config: Config = new Config();
    config.deviceAckTime = '123';
    expect(component.iconColor(config)).toEqual('color: green')
  });

  it('should return orange icon color when deviceAckTime is not defined', () => {
    const config: Config = new Config();
    expect(component.iconColor(config)).toEqual('color: orange')
  });
});
