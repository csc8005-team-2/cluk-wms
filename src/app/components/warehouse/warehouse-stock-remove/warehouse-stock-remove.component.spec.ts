import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehouseStockRemoveComponent } from './warehouse-stock-remove.component';

describe('WarehouseStockRemoveComponent', () => {
  let component: WarehouseStockRemoveComponent;
  let fixture: ComponentFixture<WarehouseStockRemoveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WarehouseStockRemoveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehouseStockRemoveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
