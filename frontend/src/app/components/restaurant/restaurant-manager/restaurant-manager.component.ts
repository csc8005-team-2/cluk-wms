import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {MatDialog, MatTableDataSource} from '@angular/material';
import {StockItem} from '../../../classes/stock-item';
import {SessionService} from '../../../services/session.service';
import {ChangeMinThresholdComponent} from '../../change-min-threshold/change-min-threshold.component';

@Component({
  selector: 'app-restaurant-manager',
  templateUrl: './restaurant-manager.component.html',
  styleUrls: ['./restaurant-manager.component.css']
})
export class RestaurantManagerComponent implements OnInit {
  // Restaurant Orders table
  currentLevel: MatTableDataSource<StockItem>;
  newLevels: StockItem[] = [];
  changeErrors: StockItem[] = [];

  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity', 'change'];

  constructor(private cdRef: ChangeDetectorRef, private session: SessionService, private dialog: MatDialog) {
    this.session.getMinStockRest(this.session.getVenueAddress()).subscribe(res => {
      this.currentLevel = new MatTableDataSource(res);
    });

  }

  changeQuantity(element: StockItem) {
    const dialogRef = this.dialog.open(ChangeMinThresholdComponent, {
      width: '250px',
      data: element
    });

    dialogRef.afterClosed().subscribe(newThresholdQty => {
      // create JSON
      const newThreshold = {stockItem: element.stockItem, quantity: newThresholdQty}
      // push to the array with changes pending
      this.newLevels.push(newThreshold);
      // update displayed table
      const currentLevelsArr = this.currentLevel.data;
      const elementIndex = currentLevelsArr.indexOf(element);
      currentLevelsArr[elementIndex] = newThreshold;
      this.currentLevel = new MatTableDataSource(currentLevelsArr);
      this.cdRef.detectChanges();
    });
  }

  submitChanges() {
    for (const element of this.newLevels) {
      this.session.updateMinStockRest(this.session.getVenueAddress(), element).subscribe(res => {}, err => {
        this.changeErrors.push(element);
      });
    }

    if (this.changeErrors.length === 0) {
      this.newLevels = [];
      this.changeErrors = [];
      window.alert('Change successful!');
    } else if (this.changeErrors.length === this.newLevels.length) {
      window.alert('Unknown error encountered. No changes made to the minimum stock levels. Try again later');
    } else {
      let errItems: string = '\n';
      for (const element of this.changeErrors) {
        errItems += element.stockItem + '\n';
      }
      window.alert('Unknown error encountered when changing stock levels of following items: ' + errItems + '\nPlease try again later!');
    }
  }

  ngOnInit() {
  }

}










