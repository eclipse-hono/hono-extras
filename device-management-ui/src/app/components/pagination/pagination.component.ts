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

import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent {
  @Input() public deviceListCount: number = 0;

  @Output() public pageNumberChange = new EventEmitter<number>();
  @Output() public pageSizeChange = new EventEmitter<number>();

  public pageSize: number = 50;
  public pageSizeOptions: number[] = [50, 100, 200];

  changePage($event: number) {
    this.pageNumberChange.emit($event)
  }
  onPageSizeChange(event: any) {
    const {value} = event.target;
    if (+value) {
      this.pageSizeChange.emit(+value);
    }
  }

}
