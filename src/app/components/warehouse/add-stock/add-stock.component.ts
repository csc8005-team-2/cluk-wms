import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { DataSourceFromTable } from '../../../classes/table-data-source';
import {StockItem} from '../../../classes/stock-item';
import {SessionService} from '../../../services/session.service';
export interface OrderItem {
  stockItem: string;
  quantity: number;
}

@Component({
  selector: 'app-new-order',
  templateUrl: './add-stock.component.html',
  styleUrls: ['./add-stock.component.css']
})
export class AddStockComponent implements OnInit {
  // Stock Object table
  availableIngredients: StockItem[] = [
    {stockItem: 'Shredded Iceberg Lettuce', quantity: 123434},
    {stockItem: 'Cheese Slices', quantity: 123434},
    {stockItem: 'Chicken Pieces', quantity: 123434},
    {stockItem: 'Sesame seed buns', quantity: 123434},
    {stockItem: 'Chicken Brest Fillets', quantity: 123434},
    {stockItem: 'Chicken Strips', quantity: 123434}
  ];

  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity'];

  // defining data source for the table
  // could be done simply using an array 'order' but then it would not be dynamic
  // and won't refresh on table change
  order: OrderItem[] = [];
  orderDataSource: DataSourceFromTable = new DataSourceFromTable(this.order);
  incorrectSelection = false;

  addItem(stockItem: string, qty: number) {
    if (stockItem && qty) {
      this.incorrectSelection = false;
      this.order.push({stockItem, quantity: qty});
      this.orderDataSource = new DataSourceFromTable(this.order);
      this.cdRef.detectChanges();
    } else {
      this.incorrectSelection = true;
    }
  }

  constructor(private cdRef: ChangeDetectorRef, private session: SessionService) { }

  ngOnInit() {
  }

  addToWarehouseStock() {
    this.session.updateStockWar(this.session.getVenueAddress(), this.order).subscribe(res => {
      if (res.message === 'STOCK_UPDATED') {
        this.order = [];
        this.orderDataSource = new DataSourceFromTable(this.order);
        window.alert('Stock updated!');
      }
    }, err => {
      console.log(err);
    });
  }
}
