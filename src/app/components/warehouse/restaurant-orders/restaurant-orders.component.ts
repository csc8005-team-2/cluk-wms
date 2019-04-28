import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {SessionService} from '../../../services/session.service';
import {OrderEntry} from '../../../classes/order-entry';
@Component({
  selector: 'app-restaurant-orders',
  templateUrl: './restaurant-orders.component.html',
  styleUrls: ['./restaurant-orders.component.css']
})
export class RestaurantOrdersComponent implements OnInit {
  // Restaurant Orders table
  pendingOrders: OrderEntry[];

  // displayed columns format
  displayedColumns: string[] = ['orderId', 'dateTime', 'address'];

  
  constructor(private cdRef: ChangeDetectorRef, private session: SessionService) {
    this.session.getPendingOrders().subscribe(res => {
      this.pendingOrders = res;
    }, err => {
      console.log(err);
    });
  }

  ngOnInit() {
  }

}
