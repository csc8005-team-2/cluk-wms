import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { TableDataSource } from '../classes/table-data-source';

export interface Ingredient {
  sku: string;
  name: string;
}

export interface OrderItem {
  sku: string;
  quantity: number;
}

@Component({
  selector: 'app-new-order',
  templateUrl: './new-order.component.html',
  styleUrls: ['./new-order.component.css']
})
export class NewOrderComponent implements OnInit {
  // sample ingredient table
  availableIngredients: Ingredient[] = [
    {sku: 'steak-0', name: 'Steak'},
    {sku: 'pizza-1', name: 'Pizza'},
    {sku: 'tacos-2', name: 'Tacos'}
  ];

  // displayed columns format
  displayedColumns: string[] = ['sku', 'name', 'quantity'];

  // defining data source for the table
  // could be done simply using an array 'order' but then it would not be dynamic
  // and won't refresh on table change
  order: OrderItem[] = [];
  orderDataSource: TableDataSource = new TableDataSource(this.order);
  incorrectSelection: boolean = false;

  addItem(sku: string, qty: number) {
    if (sku && qty) {
      this.incorrectSelection = false;
    this.order.push({sku: sku, quantity: qty});
    this.orderDataSource = new TableDataSource(this.order);
    this.cdRef.detectChanges();
    } else {
      this.incorrectSelection = true;
    }
  }

  findName(sku: string): string {
    for (let ingredient of this.availableIngredients) {
      if (ingredient.sku == sku)
        return ingredient.name;
    }
  }
  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}