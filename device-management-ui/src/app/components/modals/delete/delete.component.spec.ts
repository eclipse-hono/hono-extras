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

import {DeleteComponent} from './delete.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let activeModalSpy: jasmine.SpyObj<NgbActiveModal>;

  beforeEach(async () => {
    activeModalSpy = jasmine.createSpyObj('NgbActiveModal', ['close']);
    await TestBed.configureTestingModule({
      imports: [FontAwesomeTestingModule],
      declarations: [DeleteComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [
        {provide: NgbActiveModal, useValue: activeModalSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close the active modal on cancel', () => {
    component['onCancel']();
    expect(activeModalSpy.close).toHaveBeenCalled();
  });

  it('should close the active modal on confirm and return true', () => {
    component['onConfirm']();
    expect(activeModalSpy.close).toHaveBeenCalledOnceWith(true);
  });

});
