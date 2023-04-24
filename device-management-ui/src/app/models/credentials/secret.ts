export class Secret {
  id?: string;

  enabled?: boolean;

  'not-before'?: string;

  'not-after'?: string;

  comment?: string;

  'hash-function'?: string;

  'pwd-hash'?: string;

  salt?: string;

  'pwd-plain'?: string;

  algorithm?: string = '';

  key?: string = '';

  cert?: string = '';
}
