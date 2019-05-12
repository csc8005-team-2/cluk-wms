import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {SessionService} from '../../../services/session.service';
import {Router} from '@angular/router';
import {OrderEntry} from '../../../classes/order-entry';
import {MatDialog, MatTableDataSource} from '@angular/material';
import {ViewReceivedOrderComponent} from '../view-received-order/view-received-order.component';
@Component({
  selector: 'app-receive-stock',
  templateUrl: './receive-stock.component.html',
  styleUrls: ['./receive-stock.component.css']
})
export class ReceiveStockComponent implements OnInit {
  pendingOrdersSub: any;
  pendingOrders: MatTableDataSource<OrderEntry>;

  // displayed columns format
  displayedColumns: string[] = ['orderId', 'dateTime', 'address', 'viewOrder'];

  constructor(private session: SessionService, private router: Router, private dialog: MatDialog, private cdRef: ChangeDetectorRef) {
    this.pendingOrdersSub = this.session.getTodaysOrders().subscribe(res => {
      this.pendingOrders = new MatTableDataSource(res);
    }, err => {
      console.log(err);
    });
  }

  applyFilter(filterValue: string) {
    this.pendingOrders.filter = filterValue.trim().toLowerCase();
  }

  viewOrder(order: OrderEntry[]) {
    // this.session.setOrderView(orderContents);
    const dialogRef = this.dialog.open(ViewReceivedOrderComponent, {
      width: '600px',
      data: order
    });

    dialogRef.afterClosed().subscribe(orderAccepted => {
      if (orderAccepted) {
        this.pendingOrdersSub.unsubscribe();
        this.pendingOrdersSub = this.session.getPendingOrders().subscribe(res => {
          this.pendingOrders = new MatTableDataSource(res);
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


