import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {StockName} from '../../../classes/stock-name';
import {StockItem} from '../../../classes/stock-item';
import {MatTableDataSource} from '@angular/material';
import {SessionService} from '../../../services/session.service';

@Component({
  selector: 'app-restaurant-stock-remove',
  templateUrl: './restaurant-stock-remove.component.html',
  styleUrls: ['./restaurant-stock-remove.component.css']
})
export class RestaurantStockRemoveComponent implements OnInit {

// displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity'];
  availableItems: StockName[];
  selectedItems: StockItem[] = [];

  // defining data source for the table
  // could be done simply using an array 'order' but then it would not be dynamic
  // and won't refresh on table change
  removalDataSource = new MatTableDataSource(this.selectedItems);
  incorrectSelection = false;


  constructor(private cdRef: ChangeDetectorRef, private session: SessionService) {
    this.session.getStockNames().subscribe(res => {
      this.availableItems = res;
    });
  }

  ngOnInit() {
  }

  addItem(stockItem: string, qtyStr: string) {
    if (stockItem && qtyStr) {
      const qty: number = +qtyStr;
      this.incorrectSelection = false;
      this.selectedItems.push({stockItem, quantity: qty});
      this.removalDataSource = new MatTableDataSource(this.selectedItems);
      this.cdRef.detectChanges();
    } else {
      this.incorrectSelection = true;
    }
  }
  removeFromWarehouseStock() {
    // create array of negative quantities
    const newQuantities: StockItem[] = [];

    for (const element of this.selectedItems) {
      const negQuantity = (-1) * element.quantity;
      newQuantities.push({stockItem: element.stockItem, quantity: negQuantity});
    }

    this.session.updateStockRest(this.session.getVenueAddress(), newQuantities).subscribe(res => {
      if (res.message === 'STOCK_UPDATED') {
        this.selectedItems = [];
        this.removalDataSource = new MatTableDataSource(this.selectedItems);
        window.alert('Stock updated!');
      }
    }, err => {
      console.log(err);
    });
  }
}
