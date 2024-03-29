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

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ToastContainerComponent} from './toast-container.component';
import {NotificationService} from "../../services/notification/notification.service";

describe('ToastContainerComponent', () => {
  let component: ToastContainerComponent;
  let fixture: ComponentFixture<ToastContainerComponent>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ToastContainerComponent],
      providers: [NotificationService]
    })
      .compileComponents();
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['notify']);
    component = new ToastContainerComponent(notificationServiceSpy);

    fixture = TestBed.createComponent(ToastContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return false when toast is not of instance TemplateRef', () => {
    const toast = {textOrTpl: 'some text'};
    expect(component['isTemplate'](toast)).toBeFalse();
  });

});
