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
