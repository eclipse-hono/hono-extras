import {CredentialTypes} from "./credentials/credentials";

export class AuthenticationValue {
  type?: CredentialTypes | string;

  'auth-id'?: string;

  'not-after'?: string;

  'not-before'?: string;

  algorithm?: string;

  key?: string;

  id?: string;

}
