import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatTableDataSource} from '@angular/material';
import {StockItem} from '../../../classes/stock-item';
import {OrderEntry} from '../../../classes/order-entry';
import {SessionService} from '../../../services/session.service';

@Component({
  selector: 'app-view-received-order',
  templateUrl: './view-received-order.component.html',
  styleUrls: ['./view-received-order.component.css']
})
export class ViewReceivedOrderComponent implements OnInit {
  availableStock: MatTableDataSource<StockItem>;
  orderId: number;
  orderAddress: string;

  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity'];

  applyFilter(filterValue: string) {
    this.availableStock.filter = filterValue.trim().toLowerCase();
  }

  constructor(@Inject(MAT_DIALOG_DATA) private data: OrderEntry, private session: SessionService, private dialogRef: MatDialogRef<ViewReceivedOrderComponent>) {
    this.orderId = this.data.orderId;
    this.orderAddress = this.data.address;
    this.availableStock = new MatTableDataSource(this.data.contents);
  }

  acceptOrder() {
    this.session.receiveOrder(this.orderAddress, this.orderId).subscribe(res => {
      if (res.message === 'ORDER_RECEIVED') {
        window.alert('Order ' + this.orderId + ' successfully received. Stock amounts updated in the database.');
        this.dialogRef.close(true);
      }
    }, err => console.log(err));
  }

  ngOnInit() {
  }

}
