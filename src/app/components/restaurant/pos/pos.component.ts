import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {MealPrice} from '../../../classes/meal-price';
import {SessionService} from '../../../services/session.service';
import {MealOrder} from '../../../classes/meal-order';

@Component({
  selector: 'app-pos',
  templateUrl: './pos.component.html',
  styleUrls: ['./pos.component.css']
})
export class POSComponent implements OnInit {
  // Stock Object table
  availableMeals: MealPrice[];

  // displayed columns format
  displayedColumns: string[] = ['meal', 'unitPrice', 'quantity', 'totalPrice'];

  // defining data source for the table
  // could be done simply using an array 'order' but then it would not be dynamic
  // and won't refresh on table change
  order: MealOrder[] = [];
  orderDataSource: MatTableDataSource<MealOrder> = new MatTableDataSource(this.order);
  incorrectSelection = false;
  totalPrice = 0;

  constructor(private session: SessionService, private cdRef: ChangeDetectorRef) {
    this.session.getMealNames().subscribe(res => {
      this.availableMeals = res;
    });
  }

  getTotalPrice(): number {
    let total = 0;
    for (let i = 0; i < this.order.length; i++) {
      total += (this.order[i].mealItem.price * this.order[i].quantity);
    }
    return total;
  }
  addItem(mealItem: MealPrice, qtyStr: string) {
    if (mealItem && qtyStr) {
      const quantity: number = +qtyStr;

      this.incorrectSelection = false;
      this.order.push({mealItem, quantity});
      this.orderDataSource = new MatTableDataSource(this.order);
      this.totalPrice = this.getTotalPrice();
      this.cdRef.detectChanges();
    } else {
      this.incorrectSelection = true;
    }
  }

  submitOrder() {
    let mealSub: any;
    for (let i = 0; i < this.order.length; i++) {
      const orderItem = this.order[i];
      for (let j = 0; j < orderItem.quantity; j++) {
        if (mealSub) { mealSub.unsubscribe(); }
        mealSub = this.session.createMeal(this.session.getVenueAddress(), orderItem.mealItem.meal).subscribe(res => {
          this.order = [];
          this.orderDataSource = new MatTableDataSource(this.order);
          this.totalPrice = 0;
          window.alert('Order sent to the kitchen!');
        }, err => {
          if (err.message === 'STOCK_TOO_LOW') {
            window.alert(orderItem.mealItem.meal + ' is out of stock and was removed from the order');
          }
        });
      }
    }
  }

  ngOnInit() {
  }

}
