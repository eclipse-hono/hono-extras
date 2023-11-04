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

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TenantListComponent} from '../components/tenants/tenant-list/tenant-list.component';
import {TenantDetailComponent} from '../components/tenants/tenant-detail/tenant-detail.component';
import {DeviceDetailComponent} from '../components/devices/device-detail/device-detail.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {path: 'device-detail/:tenantId/:id', component: DeviceDetailComponent},
      {path: 'tenant-detail/:id', component: TenantDetailComponent},
      {path: 'tenant-list', component: TenantListComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [],
  declarations: []
})
export class AppRoutingModule {
}
