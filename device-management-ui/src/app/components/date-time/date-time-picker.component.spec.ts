import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DateTimePickerComponent} from './date-time-picker.component';
import {NgbCalendar, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";
import {FormsModule} from "@angular/forms";

describe('DateTimePickerComponent', () => {
  let component: DateTimePickerComponent;
  let fixture: ComponentFixture<DateTimePickerComponent>;
  let calendarSpy: jasmine.SpyObj<NgbCalendar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NgbModule, FontAwesomeTestingModule, FormsModule],
      declarations: [DateTimePickerComponent],
    })
      .compileComponents();

    calendarSpy = TestBed.inject(NgbCalendar) as jasmine.SpyObj<NgbCalendar>;
    fixture = TestBed.createComponent(DateTimePickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit date and time on onFocusOut', () => {
    spyOn(component.dateTime, 'emit');

    component.onFocusOut();

    expect(component.dateTime.emit).toHaveBeenCalledWith({
      date: component['date'],
      time: component['time'],
    });
  });

  it('should set date and time from secretDate input', () => {
    component.secretDate = '2023-05-15T10:00:00.000Z';

    component.ngOnInit();

    expect(component['date']).toEqual({ day: 15, month: 5, year: 2023 });
    expect(component['time']).toEqual({ hour: 10, minute: 0, second: 0 });
  });

  it('should set date to today and emit date and time when secretDate is not provided', () => {
    spyOn(component, 'onFocusOut');

    component.secretDate = undefined;
    fixture.detectChanges();

    component.ngOnInit();
    expect(component['date']).toEqual(component['calendar'].getToday());
    expect(component['time']).toEqual({hour: 0, minute: 0, second: 0});
    expect(component.onFocusOut).toHaveBeenCalled();
  });

  it('should return correct time string', () => {
    const time = {hour: 12, minute: 30, second: 0};

    const result = component['getTimeString'](time);
    expect(result).toEqual('12:30:00');
  });

});
