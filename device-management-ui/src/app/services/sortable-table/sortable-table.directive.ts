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

import {Directive, EventEmitter, Input, Output} from '@angular/core';

type SortDirection = 'asc' | 'desc' | '';
const rotate: { [key: string]: SortDirection } = {'asc': 'desc', 'desc': '', '': 'asc'};

export interface SortEvent {
  column: string;
  direction: SortDirection;
}

@Directive({
  selector: 'th[sortable]',
  host: {
    '[class.sort-asc]': 'direction === "asc"',
    '[class.sort-desc]': 'direction === "desc"',
    '(click)': 'rotate()'
  }
})
export class SortableTableDirective {

  @Input()
  public sortable: string = '';

  @Input()
  public direction: SortDirection = '';

  @Output()
  public sort = new EventEmitter<SortEvent>();

  constructor() {
  }

  rotate() {
    this.direction = rotate[this.direction];
    this.sort.emit({column: this.sortable, direction: this.direction});
  }
}
