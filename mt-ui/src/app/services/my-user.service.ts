import { Injectable } from '@angular/core';
import { TenantEntityService } from '../clazz/tenant-entity.service';
import { IProjectUser } from '../clazz/validation/aggregate/user/interfaze-user';
import { DeviceService } from './device.service';
import { HttpProxyService } from './http-proxy.service';
import { CustomHttpInterceptor } from './interceptors/http.interceptor';
@Injectable({
  providedIn: 'root'
})
export class MyUserService extends TenantEntityService<IProjectUser, IProjectUser>{
  protected entityName: string = "users";
  constructor(httpProxy: HttpProxyService, interceptor: CustomHttpInterceptor, deviceSvc: DeviceService) {
    super(httpProxy, interceptor, deviceSvc);
  }
}
