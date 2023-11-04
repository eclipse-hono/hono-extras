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

import {Component, Input, OnInit} from '@angular/core';
import {StatesService} from "../../../../services/states/states.service";
import {State} from "../../../../models/state";

@Component({
  selector: 'app-list-state',
  templateUrl: './list-state.component.html',
  styleUrls: ['./list-state.component.scss']
})
export class ListStateComponent implements OnInit {

  @Input() public deviceId: string = '';
  @Input() public tenantId: string = '';

  public states: State[] = [];

  constructor(private statesService: StatesService) {
  }

  ngOnInit() {
    this.getStates();
  }

  private getStates() {
    this.statesService.list(this.deviceId, this.tenantId).subscribe((states) => {
      this.states = states.deviceStates;
    }, (error) => {
      console.log(error);
    })
  }
}
