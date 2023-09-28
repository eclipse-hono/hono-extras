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

import {Injectable, TemplateRef} from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class NotificationService {

  public toasts: any[] = [];

  private show(textOrTpl: string | TemplateRef<any>, header: string, options: any = {}) {
    this.toasts.push({textOrTpl, header, ...options});
  }

  public success(message: string) {
    this.show(message, 'Success', {classname: 'bg-success text-light', autohide: true});
  }

  public warning(message: string) {
    this.show(message, 'Warning', {classname: 'bg-warning text-light', autohide: true});
  }

  public error(message: string) {
    this.show(message, 'Error', {classname: 'bg-danger text-light', autohide: false});
  }

  public remove(toast: any) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }
}
