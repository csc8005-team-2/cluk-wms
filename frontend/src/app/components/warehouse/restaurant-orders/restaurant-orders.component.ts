import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {SessionService} from '../../../services/session.service';
import {OrderEntry} from '../../../classes/order-entry';
import {MatDialog, MatTableDataSource} from '@angular/material';
import {ViewOrderComponent} from '../view-order/view-order.component';

@Component({
  selector: 'app-restaurant-orders',
  templateUrl: './restaurant-orders.component.html',
  styleUrls: ['./restaurant-orders.component.css']
})
export class RestaurantOrdersComponent implements OnInit {
  // restaurant orders table
  pendingOrdersSub: any;
  availableStock: MatTableDataSource<OrderEntry>;

  // displayed columns format
  displayedColumns: string[] = ['orderId', 'dateTime', 'address', 'viewOrder'];

  applyFilter(filterValue: string) {
    this.availableStock.filter = filterValue.trim().toLowerCase();
  }

  constructor(private cdRef: ChangeDetectorRef, private session: SessionService, private dialog: MatDialog) {
    this.pendingOrdersSub = this.session.getPendingOrders().subscribe(res => {
      this.availableStock = new MatTableDataSource(res);
    }, err => {
      console.log(err);
    });
  }

  viewOrder(order: OrderEntry[]) {
    // this.session.setOrderView(orderContents);
    const dialogRef = this.dialog.open(ViewOrderComponent, {
      width: '600px',
      data: order
    });

    dialogRef.afterClosed().subscribe(orderAccepted => {
      if (orderAccepted) {
        this.pendingOrdersSub.unsubscribe();
        this.pendingOrdersSub = this.session.getPendingOrders().subscribe(res => {
          this.availableStock = new MatTableDataSource(res);
          this.cdRef.detectChanges();
        }, err => {
          console.log(err);
        });
      }
    });
  }

  ngOnInit() {
  }

}
