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

import {Component, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {LoadingSpinnerService} from '../../services/loading-spinner/loading-spinner.service';

@Component({
  selector: 'loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loader-spinner.component.scss']
})
export class LoaderSpinnerComponent implements OnInit {

  public isLoading: boolean = false;
  private loaderSubject: Subject<boolean> = this.loaderService.isLoading;

  constructor(private loaderService: LoadingSpinnerService) {
  }

  public ngOnInit() {
    this.loaderSubject.subscribe((isLoading) => {
      setTimeout(() => {
        this.isLoading = isLoading;
      });
    })
  }

}
