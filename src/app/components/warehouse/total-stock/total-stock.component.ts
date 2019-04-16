import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

export interface StockObject {
  stockNumber: string;
  stockItem: string;
  quantity: number;
}

@Component({
  selector: 'app-total-stock',
  templateUrl: './total-stock.component.html',
  styleUrls: ['./total-stock.component.css']
})
export class TotalStockComponent implements OnInit {
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

  
  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}
