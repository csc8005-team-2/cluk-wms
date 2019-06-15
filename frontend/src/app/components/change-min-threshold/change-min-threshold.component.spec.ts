import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeMinThresholdComponent } from './change-min-threshold.component';

describe('ChangeMinThresholdComponent', () => {
  let component: ChangeMinThresholdComponent;
  let fixture: ComponentFixture<ChangeMinThresholdComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangeMinThresholdComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeMinThresholdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
