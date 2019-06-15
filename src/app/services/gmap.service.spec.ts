import { TestBed } from '@angular/core/testing';

import { GMapService} from './gmap.service';

describe('GMapServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GMapService = TestBed.get(GMapService);
    expect(service).toBeTruthy();
  });
});
