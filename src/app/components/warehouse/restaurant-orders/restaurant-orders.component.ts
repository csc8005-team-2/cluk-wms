import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { DataSourceFromTable } from "c:/Users/Nykky/Desktop/GroupProjectFiles/chickWareFront/src/app/classes/table-data-source";

export interface RestaurantOrdersObject {
  restaurantID: string;
  restaurantName: string;
  orderDate: string;
}

@Component({
  selector: 'app-restaurant-orders',
  templateUrl: './restaurant-orders.component.html',
  styleUrls: ['./restaurant-orders.component.css']
})
export class RestaurantOrdersComponent implements OnInit {
  // Restaurant Orders table
  availableIngredients: RestaurantOrdersObject[] = [
    {restaurantID: 'SSB-01', restaurantName: 'Seaton Burn Services', orderDate: '02/04/19'},
    {restaurantID: 'ATC-02', restaurantName: 'Alnwick Town Centre', orderDate: '02/04/19'},
    {restaurantID: 'NA-03', restaurantName: 'Newton Aycliffe', orderDate: '03/04/19'},
    {restaurantID: 'TTC-04', restaurantName: 'Thirsk Town Centre', orderDate: '03/04/19'},
    {restaurantID: 'WTC-05', restaurantName: 'Whitby Town Centre', orderDate: '04/04/19'}
  ];

  // displayed columns format
  displayedColumns: string[] = ['restaurantID', 'restaurantName', 'orderDate' , 'viewOrder'];
  availableItems: any;

  
  constructor(private cdRef: ChangeDetectorRef) { }

  ngOnInit() {
  }

}
