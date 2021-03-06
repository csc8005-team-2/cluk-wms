import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {StockName} from '../../../classes/stock-name';
import {StockItem} from '../../../classes/stock-item';
import {SessionService} from '../../../services/session.service';
import {MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-order-stock',
  templateUrl: './order-stock.component.html',
  styleUrls: ['./order-stock.component.css']
})
export class OrderStockComponent implements OnInit {
  // Stock Object table
  availableStock: StockName[];
  tooLittleStock = false;
  customOrderStr = 'false';


  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity'];

  // defining data source for the table
  // could be done simply using an array 'order' but then it would not be dynamic
  // and won't refresh on table change
  order: StockItem[] = [];
  orderDataSource: MatTableDataSource<StockItem> = new MatTableDataSource(this.order);
  incorrectSelection = false;

  addItem(stockItem: string, qtyStr: string) {
    if (stockItem && qtyStr) {
      this.tooLittleStock = false;
      const qty: number = +qtyStr;

      if (qty < 2) {
        this.tooLittleStock = true;
        return;
      }

      this.incorrectSelection = false;
      this.order.push({stockItem, quantity: qty});
      this.orderDataSource = new MatTableDataSource(this.order);
      this.cdRef.detectChanges();
    } else {
      this.incorrectSelection = true;
    }
  }

  constructor(private cdRef: ChangeDetectorRef, private session: SessionService) {
    this.session.getStockNames().subscribe(res => {
      this.availableStock = res;
    });
  }

  ngOnInit() {
  }

  customOrder(): boolean {
    return (this.customOrderStr === 'true') ? true : false;
  }

  orderStock() {
    if (this.customOrder()) {
      this.session.requestCustomOrder(this.session.getVenueAddress(), this.order).subscribe(res => {
        this.order = [];
        this.orderDataSource = new MatTableDataSource(this.order);
        window.alert('Order has been sent to the warehouse! Order number: ' + res.orderId);
      }, err => {
        console.log(err);
      });
    } else {
      this.session.requestStandardOrder(this.session.getVenueAddress()).subscribe(res => {
        this.session.sendOrder(this.session.getVenueAddress(), res.orderId).subscribe(sendRes => {
          window.alert('Order has been sent to the warehouse! Order number: ' + res.orderId);
        }, err => {
          if (err.error === 'STOCK_TOO_LOW') {
            window.alert('Not enough stock in the warehouse to place order');
          }
          if (err.error === 'ORDER_ALREADY_FULFILLED') {
            window.alert('Order ' + res.orderId + ' already fulfilled!');
          }
        });
      });
    }
  }
}









