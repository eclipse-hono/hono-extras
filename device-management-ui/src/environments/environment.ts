import {Environment} from "../app/models/environment";

export const environment: Environment = {
  production: true,
  googleClientId: window['env'].GOOGLE_CLIENT_ID
};
