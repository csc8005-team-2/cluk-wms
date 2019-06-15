import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantStockRemoveComponent } from './restaurant-stock-remove.component';

describe('RestaurantStockRemoveComponent', () => {
  let component: RestaurantStockRemoveComponent;
  let fixture: ComponentFixture<RestaurantStockRemoveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantStockRemoveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantStockRemoveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
