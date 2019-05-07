import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {SessionService} from '../../../services/session.service';
import {StockItem} from '../../../classes/stock-item';
import {MatDialog, MatTableDataSource} from '@angular/material';
import {ChangeMinThresholdComponent} from '../../change-min-threshold/change-min-threshold.component';

export interface WarehouseManagerObject {
  stockNumber: string;
  stockItem: string;
  currentThreshold: string;
  newMinThreshold: string;
}

@Component({
  selector: 'app-warehouse-manager',
  templateUrl: './warehouse-manager.component.html',
  styleUrls: ['./warehouse-manager.component.css']
})
export class WarehouseManagerComponent implements OnInit {
  // Restaurant Orders table
  currentLevel: MatTableDataSource<StockItem>;
  newLevels: StockItem[] = [];
  changeErrors: StockItem[] = [];

  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity', 'change'];
  
  constructor(private cdRef: ChangeDetectorRef, private session: SessionService, private dialog: MatDialog) {
    this.session.getMinStockWar(this.session.getVenueAddress()).subscribe(res => {
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
      this.session.updateMinStockWar(this.session.getVenueAddress(), element).subscribe(res => {console.log(res); }, err => {
        console.log(err);
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
