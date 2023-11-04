/*
 * *******************************************************************************
 *  * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  *
 *  * See the NOTICE file(s) distributed with this work for additional
 *  * information regarding copyright ownership.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbCalendar, NgbDateStruct} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-date-time-picker',
  templateUrl: './date-time-picker.component.html',
  styleUrls: ['./date-time-picker.component.scss']
})
export class DateTimePickerComponent implements OnInit {

  @Input() public secretDate: string | undefined = '';

  @Output() public dateTime: EventEmitter<any> = new EventEmitter<any>();

  public date: NgbDateStruct | any;
  public time: any = {hour: 0, minute: 0, second: 0};
  public maxDate: NgbDateStruct = {year: new Date().getUTCFullYear() + 100, month: 12, day: 31};

  constructor(private calendar: NgbCalendar) {
  }

  private get timezoneOffset() {
    const date = new Date(this.date.year, this.date.month, this.date.day);
    date.setHours(Number(this.time.hour));
    date.setMinutes(Number(this.time.minute));
    date.setSeconds(this.time.second);
    return date.getTimezoneOffset() / 60;
  }

  public ngOnInit() {
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

  public showTimezoneOffsetMessage(): boolean {
    return this.timezoneOffset !== 0;
  }

  public getTimezoneOffsetMessage() {
    return 'Your timezone differ to UTC time, please be aware that the UTC time (%h hour) will be taken.'
      .replace('%h', String(this.timezoneOffset));
  }

  public getTimeString(time: any): string {
    const paddedHour = String(time.hour).padStart(2, '0');
    const paddedMinute = String(time.minute).padStart(2, '0');
    const paddedSecond = String(time.second).padStart(2, '0');

    return `${paddedHour}:${paddedMinute}:${paddedSecond}`;
  }
}
