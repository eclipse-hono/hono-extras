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

import {Component, TemplateRef} from '@angular/core';
import {NotificationService} from "../../services/notification/notification.service";

@Component({
  selector: 'toast-container',
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss'],
  host: {class: 'toast-container bottom-0 end-0 p-3'},
})
export class ToastContainerComponent {
  constructor(public notificationService: NotificationService) {
  }

  public isTemplate(toast: any) {
    return toast.textOrTpl instanceof TemplateRef;
  }
}
