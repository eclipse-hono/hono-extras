import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbCalendar, NgbDateStruct} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-date-time-picker',
  templateUrl: './date-time-picker.component.html',
  styleUrls: ['./date-time-picker.component.scss']
})
export class DateTimePickerComponent implements OnInit {

  @Input()
  public secretDate: string | undefined = '';

  @Output()
  public dateTime: EventEmitter<any> = new EventEmitter<any>();
  protected date: NgbDateStruct | any;
  protected time: any = {hour: 0, minute: 0, second: 0};

  protected maxDate: NgbDateStruct = {year: new Date().getUTCFullYear() + 100, month: 12, day: 31};

  constructor(private calendar: NgbCalendar) {
  }

  private get timezoneOffset() {
    const date = new Date(this.date.year, this.date.month, this.date.day);
    date.setHours(Number(this.time.hour));
    date.setMinutes(Number(this.time.minute));
    date.setSeconds(this.time.second);
    return date.getTimezoneOffset() / 60;
  }

  ngOnInit() {
    if (this.secretDate) {
      const date = new Date(this.secretDate);
      this.date = {day: date.getUTCDate(), month: date.getUTCMonth() + 1, year: date.getUTCFullYear()};
      this.time = {hour: date.getUTCHours(), minute: date.getUTCMinutes(), second: date.getUTCSeconds()};
    } else {
      this.date = this.calendar.getToday();
      this.onFocusOut();
    }
  }

  public onFocusOut() {
    this.dateTime.emit({
      date: this.date,
      time: this.time
    });
  }

  protected showTimezoneOffsetMessage(): boolean {
    return this.timezoneOffset !== 0;
  }

  protected getTimezoneOffsetMessage() {
    return 'Your timezone differ to UTC time, please be aware that the UTC time (%h hour) will be taken.'
      .replace('%h', String(this.timezoneOffset));
  }

  protected getTimeString(time: any): string {
    const paddedHour = String(time.hour).padStart(2, '0');
    const paddedMinute = String(time.minute).padStart(2, '0');
    const paddedSecond = String(time.second).padStart(2, '0');

    return `${paddedHour}:${paddedMinute}:${paddedSecond}`;
  }
}
