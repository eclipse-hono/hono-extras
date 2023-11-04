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

import {ModalHeadComponent} from './modal-head.component';

describe('ModalHeadComponent', () => {
  let component: ModalHeadComponent;
  let fixture: ComponentFixture<ModalHeadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModalHeadComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalHeadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit the closeModal event when cancel() is called', () => {
    spyOn(component.closeModal, 'emit');
    component['cancel']();
    expect(component.closeModal.emit).toHaveBeenCalledWith(true);
  });

});
