import { Component, OnDestroy } from '@angular/core';
import { MatBottomSheet, MatBottomSheetConfig } from '@angular/material/bottom-sheet';
import { ActivatedRoute } from '@angular/router';
import { FormInfoService } from 'mt-form-builder';
import { IForm, IOption } from 'mt-form-builder/lib/classes/template.interface';
import { combineLatest, Observable, of } from 'rxjs';
import { take } from 'rxjs/operators';
import { IBottomSheet, SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { hasValue } from 'src/app/clazz/validation/validator-common';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { FORM_CONFIG } from 'src/app/form-configs/catalog-view.config';
import { DeviceService } from 'src/app/services/device.service';
import { PermissionService } from 'src/app/services/permission.service';
import { ProjectService } from 'src/app/services/project.service';
import { PermissionComponent } from '../permission/permission.component';

@Component({
  selector: 'app-my-permissions',
  templateUrl: './my-permissions.component.html',
  styleUrls: ['./my-permissions.component.css']
})
export class MyPermissionsComponent extends SummaryEntityComponent<IPermission, IPermission> implements OnDestroy {
  public formId = "permissionTableColumnConfig";
  formId2 = 'summaryPermissionCustomerView';
  formInfo: IForm = JSON.parse(JSON.stringify(FORM_CONFIG));
  viewType: "TREE_VIEW" | "LIST_VIEW" | "DYNAMIC_TREE_VIEW" = "LIST_VIEW";
  
  public projectId: string;
  private formCreatedOb2: Observable<string>;
  columnList = {
    id: 'ID',
    name: 'NAME',
    description: 'DESCRIPTION',
    type: 'TYPE',
    edit: 'EDIT',
    clone: 'CLONE',
    delete: 'DELETE',
  }
  sheetComponent = PermissionComponent;
  public loadRoot = this.entitySvc.readByQuery(0, 1000, "parentId:null")
  public loadChildren = (id: string) => this.entitySvc.readByQuery(0, 1000, "parentId:" + id)
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
    public entitySvc: PermissionService,
    public projectSvc: ProjectService,
    public deviceSvc: DeviceService,
    public fis: FormInfoService,
    public bottomSheet: MatBottomSheet,
    private route: ActivatedRoute,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, fis, 2);
    this.route.paramMap.pipe(take(1)).subscribe(queryMaps => {
      this.projectId = queryMaps.get('id')
      this.entitySvc.queryPrefix = 'projectIds:'+this.projectId;
    });
    this.formCreatedOb2 = this.fis.formCreated(this.formId2);
    
    combineLatest([this.formCreatedOb2]).pipe(take(1)).subscribe(()=>{
      const sub = this.fis.formGroupCollection[this.formId2].valueChanges.subscribe(e => {
        this.viewType = e.view;
        if (this.viewType === 'TREE_VIEW') {
          this.entitySvc.readEntityByQuery(0, 1000).subscribe(next => {
            super.updateSummaryData(next)
          });
        } 
      });
      if(!this.fis.formGroupCollection[this.formId2].get('view').value){
        this.fis.formGroupCollection[this.formId2].get('view').setValue(this.viewType);
      }
      this.subs.add(sub)
    })
  }
  ngOnDestroy(){
    this.fis.resetAll()
  };
  getOption(value: string, options: IOption[]) {
    return options.find(e => e.value == value)
  }
  openBottomSheet(id?: string, clone?: boolean): void {
    let config = new MatBottomSheetConfig();
    config.autoFocus = true;
    config.panelClass = 'fix-height'
    if (hasValue(id)) {
      of(this.dataSource.data.find(e => e.id === id))
        .subscribe(next => {
          if (clone) {
            config.data = <IBottomSheet<IPermission>>{ context: 'clone', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          } else {
            config.data = <IBottomSheet<IPermission>>{ context: 'edit', from: next };
            this.bottomSheet.open(this.sheetComponent, config);
          }
        })
    } else {
      config.data = <IBottomSheet<IPermission>>{ context: 'new', from: { projectId: this.projectId, name: '', id: '', version: 0 } };
      this.bottomSheet.open(this.sheetComponent, config);
    }
  }
}