import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

export interface RestaurantManagerObject {
  stockNumber: string;
  stockItem: string;
  currentThreshold: string;
  newMinThreshold: string;
}


@Component({
  selector: 'app-restaurant-manager',
  templateUrl: './restaurant-manager.component.html',
  styleUrls: ['./restaurant-manager.component.css']
})
export class RestaurantManagerComponent implements OnInit {
  availableIngredients: RestaurantManagerObject[] = [
    {stockNumber: 'Let-539', stockItem: 'Shredded Iceberg Lettuce', currentThreshold: '10', newMinThreshold: '5'},
    {stockNumber: 'Chsl-157', stockItem: 'Cheese Slices', currentThreshold: '200', newMinThreshold: '150'},
    {stockNumber: 'CKP-754', stockItem: 'Chicken Pieces', currentThreshold: '100', newMinThreshold: '100'},
    {stockNumber: 'SSB-279', stockItem: 'Sesame seed buns', currentThreshold: '10', newMinThreshold: '5'},
    {stockNumber: 'CKF-412', stockItem: 'Chicken Brest Fillets', currentThreshold: '100', newMinThreshold: '100'},
    {stockNumber: 'CKS-367', stockItem: 'Chicken Strips', currentThreshold: '100', newMinThreshold: '80'}
  ];

  // displayed columns format
  displayedColumns: string[] = ['stockNumber', 'stockItem', 'currentThreshold' , 'newMinThreshold'];
  availableItems: any;

  
  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}










