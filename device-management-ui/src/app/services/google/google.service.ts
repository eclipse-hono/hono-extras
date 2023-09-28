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

import {Injectable} from '@angular/core';
import {AuthConfig, OAuthService} from "angular-oauth2-oidc";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";

const oAuthConfig: AuthConfig = {
  issuer: 'https://accounts.google.com',
  strictDiscoveryDocumentValidation: false,
  redirectUri: window.location.origin,
  clientId: environment.googleClientId,
  scope: 'openid profile email'
};

@Injectable({
  providedIn: 'root'
})
export class GoogleService {

  constructor(private oauthService: OAuthService, private router: Router) {
    if (!this.oauthService.hasValidAccessToken()) {
      this.oauthService.configure(oAuthConfig);
      this.oauthService.loadDiscoveryDocument().then(() => {
        this.oauthService.tryLoginImplicitFlow().then(() => {
          if (!this.oauthService.hasValidAccessToken()) {
            console.log('No valid access token, starting implicit flow.');
            this.oauthService.initLoginFlow()
          } else {
            this.oauthService.loadUserProfile().then((user) => {
              this.router.navigate(['/tenant-list']);
            });
          }
        }, (error) => {
          console.log(error);
        });
      }, (error) => {
        console.log(error);
      });
    }
  }

  public getIdToken() {
    return this.oauthService.getIdToken();
  }

}
