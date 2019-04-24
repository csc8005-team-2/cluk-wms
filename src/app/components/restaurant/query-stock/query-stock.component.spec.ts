import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { QueryStockComponent } from './query-stock.component';

describe('QueryStockComponent', () => {
  let component: QueryStockComponent;
  let fixture: ComponentFixture<QueryStockComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryStockComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryStockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
