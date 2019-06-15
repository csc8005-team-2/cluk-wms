import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DispatchChartComponent } from './dispatch-chart.component';

describe('DispatchChartComponent', () => {
  let component: DispatchChartComponent;
  let fixture: ComponentFixture<DispatchChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DispatchChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DispatchChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
