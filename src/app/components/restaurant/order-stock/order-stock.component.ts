import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

export interface OrderStockObject {
  stockNumber: string;
  stockItem: string;
  orderAmount: number;
}

@Component({
  selector: 'app-order-stock',
  templateUrl: './order-stock.component.html',
  styleUrls: ['./order-stock.component.css']
})
export class TotalStockComponent implements OnInit {
  // Stock Object table
  availableIngredients: OrderStockObject[] = [
    {stockNumber: 'Let-539', stockItem: 'Shredded Iceberg Lettuce', orderAmount: 123434},
    {stockNumber: 'Chsl-157', stockItem: 'Cheese Slices', orderAmount: 123434},
    {stockNumber: 'CKP-754', stockItem: 'Chicken Pieces', orderAmount: 123434},
    {stockNumber: 'SSB-279', stockItem: 'Sesame seed buns', orderAmount: 123434},
    {stockNumber: 'CKF-412', stockItem: 'Chicken Brest Fillets',orderAmount: 123434},
    {stockNumber: 'CKS-367', stockItem: 'Chicken Strips', orderAmount: 123434}
  ];

  // displayed columns format
  displayedColumns: string[] = ['stockNumber', 'stockItem', 'orderAmount'];
  availableItems: any;

  
  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}









