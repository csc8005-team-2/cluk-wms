import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material';
import {StockItem} from '../../classes/stock-item';

@Component({
  selector: 'app-change-min-threshold',
  templateUrl: './change-min-threshold.component.html',
  styleUrls: ['./change-min-threshold.component.css']
})
export class ChangeMinThresholdComponent implements OnInit {
  stockItem: StockItem;

  constructor(@Inject(MAT_DIALOG_DATA) private data: StockItem) {
    this.stockItem = data;
  }

  ngOnInit() {
  }

}
