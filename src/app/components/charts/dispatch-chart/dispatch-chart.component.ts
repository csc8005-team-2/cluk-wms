import {Component, ElementRef, NgModule, OnInit, ViewChild} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import {SessionService} from '../../../services/session.service';
import {StockItem} from '../../../classes/stock-item';
import {StockName} from '../../../classes/stock-name';
import {GraphData} from '../../../classes/graph-data';

@Component({
  selector: 'app-dispatch-chart',
  templateUrl: './dispatch-chart.component.html',
  styleUrls: ['./dispatch-chart.component.css']
})
export class DispatchChartComponent implements OnInit {
  availableStock: StockName[];
  timeIntervalModel = 'week';
  selectedStockItemModel: string;
  graphData: GraphData[];
  graphSub: any;
  showChart = false;


  single: any[];
  multi: any[];

  view: any[] = [700, 400];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Restaurant';
  showYAxisLabel = true;
  yAxisLabel = 'Quantity';

  colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
  };

  constructor(private session: SessionService) {
    this.session.getStockNames().subscribe(res => {
      this.availableStock = res;
    });

    Object.assign(this, this.graphData);
  }

  updateGraph() {
    this.showChart = true;
    // this.graphSub.unsubscribe();
    this.graphSub = this.session.getWarehouseGraph(this.selectedStockItemModel, this.timeIntervalModel).subscribe((res) => {
      this.graphData = res;
    });

  }

  ngOnInit() {
  }

}
