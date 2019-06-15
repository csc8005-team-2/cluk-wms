import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewReceivedOrderComponent } from './view-received-order.component';

describe('ViewReceivedOrderComponent', () => {
  let component: ViewReceivedOrderComponent;
  let fixture: ComponentFixture<ViewReceivedOrderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewReceivedOrderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewReceivedOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
