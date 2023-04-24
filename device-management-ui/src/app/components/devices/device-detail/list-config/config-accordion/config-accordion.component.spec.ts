import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfigAccordionComponent} from './config-accordion.component';
import {NgbAccordion, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {TruncatePipe} from "../../../../../shared/truncate.pipe";

describe('ConfigAccordionComponent', () => {
  let component: ConfigAccordionComponent;
  let fixture: ComponentFixture<ConfigAccordionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfigAccordionComponent, TruncatePipe],
      imports: [NgbAccordion, NgbModule]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ConfigAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show text data', () => {
    component.config = {
      binaryData: 'VGVzdCBkYXRh', cloudUpdateTime: '', version: '', deviceAckTime: ''
    };
    component['showTextData'](true);
    expect(component['fullConfig']).toEqual('Test data');
  });

  it('should hide text data', () => {
    component['fullConfig'] = 'Test data';
    component.config = {
      binaryData: 'VGVzdCBkYXRh', cloudUpdateTime: '', version: '', deviceAckTime: ''
    };
    component['showTextData'](false);
    expect(component['fullConfig']).toEqual('VGVzdCBkYXRh');
  });

});
