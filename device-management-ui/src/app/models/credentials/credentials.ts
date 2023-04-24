import {Secret} from "./secret";

export class Credentials {
  type?: CredentialTypes | string;
  'auth-id'?: string;
  enabled?: boolean;
  ext?: any;
  secrets: Secret[] = [];
}

export enum CredentialTypes {
  HASHED_PASSWORD = 'hashed-password',
  RPK = 'rpk'
}
