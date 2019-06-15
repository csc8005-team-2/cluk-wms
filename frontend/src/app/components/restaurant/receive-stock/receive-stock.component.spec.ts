import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiveStockComponent } from './receive-stock.component';

describe('ReceiveStockComponent', () => {
  let component: ReceiveStockComponent;
  let fixture: ComponentFixture<ReceiveStockComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReceiveStockComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiveStockComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
