import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatTableDataSource} from '@angular/material';
import {OrderEntry} from '../../../classes/order-entry';
import {StockItem} from '../../../classes/stock-item';
import {SessionService} from '../../../services/session.service';

@Component({
  selector: 'app-view-order',
  templateUrl: './view-order.component.html',
  styleUrls: ['./view-order.component.css']
})
export class ViewOrderComponent implements OnInit {
  // Stock Object table
  availableStock: MatTableDataSource<StockItem>;
  orderId: number;
  orderAddress: string;

  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity'];

  applyFilter(filterValue: string) {
    this.availableStock.filter = filterValue.trim().toLowerCase();
  }

  constructor(@Inject(MAT_DIALOG_DATA) private data: OrderEntry, private session: SessionService, private dialogRef: MatDialogRef<ViewOrderComponent>) {
    this.orderId = this.data.orderId;
    this.orderAddress = this.data.address;
    this.availableStock = new MatTableDataSource(this.data.contents);
  }

  acceptOrder() {
    this.session.sendOrder(this.orderAddress, this.orderId).subscribe(res => {
      if (res.message === 'ORDER_SENT') {
        window.alert('Order ' + this.orderId + ' has been accepted for dispatch to ' + this.orderAddress + '!');
        this.dialogRef.close(true);
      }
    }, err => console.log(err));
  }

  declineOrder() {
    this.session.declineOrder(this.orderId).subscribe(res => {
      if (res.message === 'DECLINED_ORDER') {
        window.alert('Order ' + this.orderId + ' has been accepted for dispatch to ' + this.orderAddress + '!');
        this.dialogRef.close(true);
      }
    }, err => console.log(err));
  }

  ngOnInit() {
  }

}
