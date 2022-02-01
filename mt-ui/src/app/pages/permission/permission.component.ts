import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatBottomSheetRef, MAT_BOTTOM_SHEET_DATA } from '@angular/material/bottom-sheet';
import { FormInfoService } from 'mt-form-builder';
import { Aggregate } from 'src/app/clazz/abstract-aggregate';
import { IBottomSheet } from 'src/app/clazz/summary.component';
import { IPermission } from 'src/app/clazz/validation/aggregate/permission/interface-permission';
import { PermissionValidator } from 'src/app/clazz/validation/aggregate/permission/validator-permission';
import { ErrorMessage } from 'src/app/clazz/validation/validator-common';
import { FORM_CONFIG } from 'src/app/form-configs/permission.config';
import { PermissionService } from 'src/app/services/permission.service';

@Component({
  selector: 'app-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css']
})
export class PermissionComponent extends Aggregate<PermissionComponent, IPermission> implements OnInit, OnDestroy {
  bottomSheet: IBottomSheet<IPermission>;
  constructor(
    public entityService: PermissionService,
    fis: FormInfoService,
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    bottomSheetRef: MatBottomSheetRef<PermissionComponent>,
    cdr: ChangeDetectorRef
  ) {
    super('permission-form', JSON.parse(JSON.stringify(FORM_CONFIG)), new PermissionValidator(), bottomSheetRef, data, fis, cdr)
    this.bottomSheet = data;

    this.fis.queryProvider[this.formId + '_' + 'parentId'] = entityService;
    this.fis.formCreated(this.formId).subscribe(() => {
      if (this.bottomSheet.context === 'new') {
        this.fis.formGroupCollection[this.formId].get('projectId').setValue(this.bottomSheet.from.projectId)
      }
    })
  }
  ngOnDestroy(): void {
    this.fis.resetAllExcept(['summaryPermissionCustomerView'])
  }
  ngOnInit() {
  }
  convertToPayload(cmpt: PermissionComponent): IPermission {
    let formGroup = cmpt.fis.formGroupCollection[cmpt.formId];
    return {
      id: formGroup.get('id').value,//value is ignored
      name: formGroup.get('name').value,
      projectId: formGroup.get('projectId').value,
      version: cmpt.aggregate && cmpt.aggregate.version
    }
  }
  update() {
    this.entityService.update(this.aggregate.id, this.convertToPayload(this), this.changeId)
  }
  create() {
    this.entityService.create(this.convertToPayload(this), this.changeId)
  }
  errorMapper(original: ErrorMessage[], cmpt: PermissionComponent) {
    return original.map(e => {
      return {
        ...e,
        formId: cmpt.formId
      }
    })
  }
}
