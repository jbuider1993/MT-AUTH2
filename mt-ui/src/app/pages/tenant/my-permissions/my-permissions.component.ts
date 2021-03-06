import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { ISumRep, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { TenantSummaryEntityComponent } from 'src/app/clazz/tenant-summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { IProjectSimple } from 'src/app/clazz/validation/aggregate/project/interface-project';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/view-less.config';
import { DeviceService } from 'src/app/services/device.service';
import { EndpointService } from 'src/app/services/endpoint.service';
import { HttpProxyService } from 'src/app/services/http-proxy.service';
import { MyPermissionService } from 'src/app/services/my-permission.service';
import { ProjectService } from 'src/app/services/project.service';
import { PermissionComponent } from '../permission/permission.component';

@Component({
  selector: 'app-my-permissions',
  templateUrl: './my-permissions.component.html',
  styleUrls: ['./my-permissions.component.css']
})
export class MyPermissionsComponent extends TenantSummaryEntityComponent<IPermission, IPermission> implements OnDestroy {
  public formId = "permissionTableColumnConfig";
  formId2 = 'summaryPermissionCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  public apiRootId: string;
  private formCreatedOb2: Observable<string>;
  columnList:any={};
  sheetComponent = PermissionComponent;
  public loadRoot;
  public loadChildren = (id: string) => {
    if (id === this.apiRootId) {
      return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id).pipe(switchMap(data => {
        const epIds = data.data.map(e => e.name)
        return this.epSvc.readEntityByQuery(0, epIds.length, 'ids:' + epIds.join('.')).pipe(switchMap(resp => {
          data.data.forEach(e => e.name = resp.data.find(ee => ee.id === e.name).description)
          return of(data)
        }))
      }))
    } else {
      return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id)
    }
  }
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter: '.'
      }
    },
  ]
  constructor(
    public entitySvc: MyPermissionService,
    public epSvc: EndpointService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public httpSvc: HttpProxyService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    public route: ActivatedRoute,
    private translate: TranslateService
  ) {
    super(route, projectSvc, httpSvc, entitySvc, deviceSvc, bottomSheet, fis, 2);
    const sub=this.projectId.subscribe(next => {
      this.entitySvc.setProjectId(next);
      this.loadRoot = this.entitySvc.readEntityByQuery(0, 1000, "parentId:null");
      this.loadChildren = (id: string) => {
        return this.entitySvc.readEntityByQuery(0, 1000, "parentId:" + id)
      }
    });
    const sub2 = this.canDo('VIEW_PERMISSION').subscribe(b => {
      if (b.result) {
        this.doSearch({ value: '', resetPage: true })
      }
    })
    const sub3 = this.canDo('EDIT_PERMISSION').subscribe(b => {
      this.columnList = b.result? {
        id: 'ID',
        name: 'NAME',
        description: 'DESCRIPTION',
        type: 'TYPE',
        edit: 'EDIT',
        clone: 'CLONE',
        delete: 'DELETE',
      }:{
        id: 'ID',
        name: 'NAME',
        description: 'DESCRIPTION',
        type: 'TYPE',
      }
    })
    this.subs.add(sub)
    this.subs.add(sub2)
    this.subs.add(sub3)
    this.formCreatedOb2 = this.fis.formCreated(this.formId2);

    combineLatest([this.formCreatedOb2]).pipe(take(1)).subscribe(() => {
      const sub = this.fis.formGroupCollection[this.formId2].valueChanges.subscribe(e => {
        this.viewType = e.view;
      });
      if (!this.fis.formGroupCollection[this.formId2].get('view').value) {
        this.fis.formGroupCollection[this.formId2].get('view').setValue(this.viewType);
      }
      this.subs.add(sub)
    })
  }
  ngOnDestroy() {
    this.fis.reset(this.formId)
    this.fis.reset(this.formId2)
  };
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
}