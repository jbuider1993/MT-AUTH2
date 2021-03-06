import { IIdName } from "mt-form-builder/lib/classes/template.interface";

export enum grantTypeEnums {
  refresh_token = 'REFRESH_TOKEN',
  password = 'PASSWORD',
  client_credentials = 'CLIENT_CREDENTIALS',
  authorization_code = 'AUTHORIZATION_CODE'
}
export enum CLIENT_TYPE {
  root_app = 'ROOT_APPLICATION',
  firstParty = 'FIRST_PARTY',
  thirdParty = 'THIRD_PARTY',
  backend_app = 'BACKEND_APP',
  frontend_app = 'FRONTEND_APP'
}
export enum scopeEnums {
  read = 'READ',
  write = 'WRITE',
  trust = 'TRUST'
}
export interface IClient extends IIdName {
  name: string;
  path?: string;
  id: string;
  clientSecret?: string;
  projectId: string;
  description?: string;
  grantTypeEnums: grantTypeEnums[];
  types: CLIENT_TYPE[];
  accessTokenValiditySeconds: number;
  refreshTokenValiditySeconds: number;
  resourceIds: string[]
  resources?: { name: string, id: string }[]
  hasSecret: boolean;
  resourceIndicator: boolean;
  registeredRedirectUri: string[];
  autoApprove?: boolean;
  version: number;
}