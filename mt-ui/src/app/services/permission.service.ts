import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { EntityCommonService } from '../clazz/entity.common-service';
import { IPermission } from '../clazz/validation/aggregate/permission/interface-permission';
import { IRole } from '../clazz/validation/aggregate/role/interface-role';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class PermissionService extends EntityCommonService<IPermission, IPermission>{
  private ENTITY_NAME = '/auth-svc/permissions';
  entityRepo: string = environment.serverUri + this.ENTITY_NAME;
  role: string = '';
  queryPrefix = undefined;
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor,deviceSvc:DeviceService) {
    super(httpProxy, interceptor,deviceSvc);
  }
  readByQuery(num: number, size: number, query?: string, by?: string, order?: string, header?: {}){
    return this.httpProxySvc.readEntityByQuery<IRole>(this.entityRepo, this.role, num, size,query, by, order, header)
 }
 readEntityByQuery(num: number, size: number, query?: string, by?: string, order?: string, headers?: {}) {
    return this.httpProxySvc.readEntityByQuery<IPermission>(this.entityRepo, this.role, num, size, query ? (this.queryPrefix + ','+query) : this.queryPrefix, by, order, headers)
  };
}
