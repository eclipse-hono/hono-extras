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
import {BrowserModule} from '@angular/platform-browser';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './routing/app-routing.module';

import {TenantListComponent} from './components/tenants/tenant-list/tenant-list.component';
import {TenantDetailComponent} from './components/tenants/tenant-detail/tenant-detail.component';
import {DeviceListComponent} from './components/devices/device-list/device-list.component';
import {DeviceDetailComponent} from './components/devices/device-detail/device-detail.component';
import {TenantModalComponent} from './components/modals/tenant/tenant-modal.component';
import {DeleteComponent} from './components/modals/delete/delete.component';

import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {UpdateConfigModalComponent} from './components/modals/update-config-modal/update-config-modal.component';
import {SendCommandComponent} from './components/modals/send-command/send-command.component';
import {ListConfigComponent} from './components/devices/device-detail/list-config/list-config.component';
import {ListStateComponent} from './components/devices/device-detail/list-state/list-state.component';
import {FormsModule} from "@angular/forms";
import {NgSelectModule} from "@ng-select/ng-select";
import {ModalFooterComponent} from './components/modals/modal-footer/modal-footer.component';
import {ModalHeadComponent} from './components/modals/modal-head/modal-head.component';
import {
  DevicePasswordModalComponent
} from './components/modals/credentials-modal/device-password-modal/device-password-modal.component';
import {
  DeviceRpkModalComponent
} from './components/modals/credentials-modal/device-rpk-modal/device-rpk-modal.component';
import {SearchFilterPipe} from './shared/search-filter.pipe';
import {DateTimePickerComponent} from './components/date-time/date-time-picker.component';
import {DatePipe} from "@angular/common";
import {
  ListAuthenticationComponent
} from './components/devices/device-detail/list-authentication/list-authentication.component';
import {CredentialsModalComponent} from './components/modals/credentials-modal/credentials-modal.component';
import {LoaderSpinnerComponent} from './components/loading-spinner/loader-spinner.component';
import {LoadingSpinnerService} from './services/loading-spinner/loading-spinner.service';
import {LoaderInterceptor} from './shared/loader.interceptor';
import {OAuthModule} from "angular-oauth2-oidc";
import {SortableTableDirective} from './services/sortable-table/sortable-table.directive';
import {FaIconLibrary, FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {fas} from "@fortawesome/free-solid-svg-icons";
import {ToastContainerComponent} from './components/toast-container/toast-container.component';
import {TruncatePipe} from './shared/truncate.pipe';
import {GatewayListComponent} from './components/gateways/gateway-list/gateway-list.component';
import { SelectDevicesComponent } from './components/modals/select-devices/select-devices.component';
import { CreateAndBindModalComponent } from './components/modals/create-and-bind-modal/create-and-bind-modal.component';
import { PaginationComponent } from './components/pagination/pagination.component';

@NgModule({
  declarations: [
    AppComponent,
    TenantListComponent,
    TenantDetailComponent,
    DeviceListComponent,
    DeviceDetailComponent,
    TenantModalComponent,
    DeleteComponent,
    UpdateConfigModalComponent,
    SendCommandComponent,
    ListConfigComponent,
    ListStateComponent,
    ModalFooterComponent,
    ModalHeadComponent,
    DevicePasswordModalComponent,
    DeviceRpkModalComponent,
    SearchFilterPipe,
    DateTimePickerComponent,
    ListAuthenticationComponent,
    CredentialsModalComponent,
    LoaderSpinnerComponent,
    SortableTableDirective,
    ToastContainerComponent,
    TruncatePipe,
    GatewayListComponent,
    SelectDevicesComponent,
    CreateAndBindModalComponent,
    PaginationComponent
  ],
  imports: [
    BrowserModule,
    NgbModule,
    AppRoutingModule,
    HttpClientModule,
    NgSelectModule,
    FormsModule,
    OAuthModule.forRoot(),
    FontAwesomeModule
  ],
  providers: [
    DatePipe,
    LoadingSpinnerService,
    {provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor(library: FaIconLibrary) {
    library.addIconPacks(fas);
  }
}
