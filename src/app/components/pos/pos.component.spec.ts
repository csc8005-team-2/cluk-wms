import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { POSComponent } from './pos.component';

describe('POSComponent', () => {
  let component: POSComponent;
  let fixture: ComponentFixture<POSComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ POSComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(POSComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
