import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { DataSourceFromTable } from "../../../classes/table-data-source";

export interface StockObject {
  stockNumber: string;
  stockItem: string;
  quantity: number;
}

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
  availableIngredients: StockObject[] = [
    {stockNumber: 'Let-539', stockItem: 'Shredded Iceberg Lettuce', quantity: 123434},
    {stockNumber: 'Chsl-157', stockItem: 'Cheese Slices', quantity: 123434},
    {stockNumber: 'CKP-754', stockItem: 'Chicken Pieces', quantity: 123434},
    {stockNumber: 'SSB-279', stockItem: 'Sesame seed buns', quantity: 123434},
    {stockNumber: 'CKF-412', stockItem: 'Chicken Brest Fillets', quantity: 123434},
    {stockNumber: 'CKS-367', stockItem: 'Chicken Strips', quantity: 123434}
  ];

  // displayed columns format
  displayedColumns: string[] = ['stockNumber', 'stockItem', 'quantity'];
  availableItems: any;


  // defining data source for the table
  // could be done simply using an array 'order' but then it would not be dynamic
  // and won't refresh on table change
  order: OrderItem[] = [];
  orderDataSource: DataSourceFromTable = new DataSourceFromTable(this.order);
  incorrectSelection: boolean = false;

  addItem(stockItem: string, qty: number) {
    if (stockItem && qty) {
      this.incorrectSelection = false;
    this.order.push({stockItem: stockItem, quantity: qty});
    this.orderDataSource = new DataSourceFromTable(this.order);
    this.cdRef.detectChanges();
    } else {
      this.incorrectSelection = true;
    }
  }

  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}
