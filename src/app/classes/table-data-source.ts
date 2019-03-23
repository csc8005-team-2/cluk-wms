import { DataSource } from '@angular/cdk/table';
import { Observable } from 'rxjs';
import { of } from 'rxjs';

export class TableDataSource extends DataSource<any> {

    constructor(private data) {
      super();
    }
  
    connect(): Observable<any> {
      return of(this.data);
    }
  
    disconnect() {
      // No-op
    }
  
  }{
}
