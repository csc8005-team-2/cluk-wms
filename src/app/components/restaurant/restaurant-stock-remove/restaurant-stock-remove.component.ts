import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

export interface WarehouseStockRemoveObject {
  stockNumber: string;
  stockItem: string;
  currentAmount: string;
  amountRemove: string;
}

@Component({
  selector: 'app-restaurant-stock-remove',
  templateUrl: './restaurant-stock-remove.component.html',
  styleUrls: ['./restaurant-stock-remove.component.css']
})
export class RestaurantStockRemoveComponent implements OnInit {
availableIngredients: WarehouseStockRemoveObject[] = [
  {stockNumber: 'Let-539', stockItem: 'Shredded Iceberg Lettuce', currentAmount: '32', amountRemove: '5'},
  {stockNumber: 'Chsl-157', stockItem: 'Cheese Slices', currentAmount: '600', amountRemove: '10'},
  {stockNumber: 'CKP-754', stockItem: 'Chicken Pieces', currentAmount: '180', amountRemove: '0'},
  {stockNumber: 'SSB-279', stockItem: 'Sesame seed buns', currentAmount: '20', amountRemove: '3'},
  {stockNumber: 'CKF-412', stockItem: 'Chicken Brest Fillets', currentAmount: '240', amountRemove: '0'},
  {stockNumber: 'CKS-367', stockItem: 'Chicken Strips', currentAmount: '150', amountRemove: '8'}
];

// displayed columns format
displayedColumns: string[] = ['stockNumber', 'stockItem', 'currentAmount' , 'amountRemover'];
availableItems: any;


constructor(private cdRef: ChangeDetectorRef) { }

ngOnInit() {
}

}
