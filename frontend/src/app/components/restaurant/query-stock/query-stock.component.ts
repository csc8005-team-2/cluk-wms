import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {SessionService} from '../../../services/session.service';
import {MatTableDataSource} from '@angular/material';
import {ComparativeStockItem} from '../../../classes/comparative-stock-item';

@Component({
  selector: 'app-total-stock',
  templateUrl: './query-stock.component.html',
  styleUrls: ['./query-stock.component.css']
})
export class QueryStockComponent implements OnInit {
  // Stock Object table
  availableStock: MatTableDataSource<ComparativeStockItem>;

  // displayed columns format
  displayedColumns: string[] = ['stockItem', 'quantity'];

  applyFilter(filterValue: string) {
    this.availableStock.filter = filterValue.trim().toLowerCase();
  }

  constructor(private cdRef: ChangeDetectorRef, private session: SessionService) {
    this.session.getTotalStockRest(this.session.getVenueAddress()).subscribe(res => {
      this.availableStock = new MatTableDataSource(res);
    }, err => {
      console.log(err);
    });
  }

  ngOnInit() {
  }

}
