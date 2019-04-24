import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { DataSourceFromTable } from "../../../classes/table-data-source";

export interface ReceiveStockObject {
  stockNumber: string;
  stockItem: string;
  previousAmount: number;
  newTotalAmount: number;
}

@Component({
  selector: 'app-receive-stock',
  templateUrl: './receive-stock.component.html',
  styleUrls: ['./receive-stock.component.css']
})
export class ReceiveStockComponent implements OnInit {
  availableIngredients: ReceiveStockObject[] = [
    {stockNumber: 'Let-539', stockItem: 'Shredded Iceberg Lettuce', previousAmount: 10, newTotalAmount: 5},
    {stockNumber: 'Chsl-157', stockItem: 'Cheese Slices', previousAmount: 200, newTotalAmount: 150},
    {stockNumber: 'CKP-754', stockItem: 'Chicken Pieces', previousAmount: 100, newTotalAmount: 100},
    {stockNumber: 'SSB-279', stockItem: 'Sesame seed buns', previousAmount: 10, newTotalAmount: 5},
    {stockNumber: 'CKF-412', stockItem: 'Chicken Brest Fillets', previousAmount: 100, newTotalAmount: 100},
    {stockNumber: 'CKS-367', stockItem: 'Chicken Strips', previousAmount: 100, newTotalAmount: 80}
  ];

  // displayed columns format
  displayedColumns: string[] = ['stockNumber', 'stockItem', 'previousAmount' , 'newTotalAmount'];
  availableItems: any;

  
  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}


