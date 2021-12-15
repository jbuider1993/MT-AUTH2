import { Component } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatDialog } from '@angular/material/dialog';
import { IOption } from 'mt-form-builder/lib/classes/template.interface';
import { SummaryEntityComponent } from 'src/app/clazz/summary.component';
import { ICartItem, IOrder } from 'src/app/clazz/validation/interfaze-common';
import { DeviceService } from 'src/app/services/device.service';
import { OrderService } from 'src/app/services/order.service';
import { OrderComponent } from '../order/order.component';
import * as UUID from 'uuid/v1';
import { ISearchConfig } from 'src/app/components/search/search.component';
import { ORDER_STATUS } from 'src/app/clazz/constants';
@Component({
  selector: 'app-summary-order',
  templateUrl: './summary-order.component.html',
})
export class SummaryOrderComponent extends SummaryEntityComponent<IOrder, IOrder> {
  displayedColumns: string[] = ['id', 'productList', 'paymentAmt', 'orderState', 'createdAt', 'createdBy', 'view', 'delete'];
  searchConfigs: ISearchConfig[] = [
    {
      searchLabel: 'ID',
      searchValue: 'id',
      type: 'text',
      multiple: {
        delimiter:'.'
      }
    },
    {
      searchLabel: 'SEARCH_BY_ORDER_STATUS',
      searchValue: 'status',
      type: 'dropdown',
      source: ORDER_STATUS
    }
  ]
  sheetComponent = OrderComponent;
  constructor(
    public entitySvc: OrderService,
    public deviceSvc: DeviceService,
    public bottomSheet: MatBottomSheet,
    public dialog: MatDialog,
  ) {
    super(entitySvc, deviceSvc, bottomSheet, 1, false);
  }
  public parse(items: ICartItem[]): IOption[] {
    return items.map(e => <IOption>{ label: e.name, value: e.productId });
  }
  doDeleteByIdAndVersion(id: string, version: number) {
    this.entitySvc.deleteVersionedById(id, UUID(), version)
  }
}
