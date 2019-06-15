import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {StockItem} from '../../../classes/stock-item';
import {SessionService} from '../../../services/session.service';
import {StockName} from '../../../classes/stock-name';
import {MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-new-order',
  templateUrl: './add-stock.component.html',
  styleUrls: ['./add-stock.component.css']
})
export class AddStockComponent implements OnInit {
  // Stock Object table
  availableStock: StockName[];
  tooLittleStock = false;


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

  addToWarehouseStock() {
    this.session.updateStockWar(this.session.getVenueAddress(), this.order).subscribe(res => {
      if (res.message === 'STOCK_UPDATED') {
        this.order = [];
        this.orderDataSource = new MatTableDataSource(this.order);
        window.alert('Stock updated!');
      }
    }, err => {
      console.log(err);
    });
  }
}
