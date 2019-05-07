import {MatGridListModule} from '@angular/material/grid-list';
import {MatSidenavModule} from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import {MatCardModule} from '@angular/material/card';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button'; 
import {MatInputModule} from '@angular/material/input'; 
import {MatDividerModule} from '@angular/material/divider'; 
import {MatIconModule} from '@angular/material/icon'; 
import {MatExpansionModule} from '@angular/material/expansion';
import {MatSelectModule} from '@angular/material/select'; 
import {MatTableModule} from '@angular/material/table';
import {NgModule} from '@angular/core';
import {MatDialogModule} from '@angular/material';

@NgModule({
    imports: [
        MatGridListModule,
        MatSidenavModule,
        MatToolbarModule,
        MatCardModule,
        MatMenuModule,
        MatButtonModule,
        MatInputModule,
        MatDividerModule,
        MatIconModule,
        MatExpansionModule,
        MatSelectModule,
        MatTableModule,
        MatDialogModule ],
    exports: [
        MatGridListModule,
        MatSidenavModule,
        MatToolbarModule,
        MatCardModule,
        MatMenuModule,
        MatButtonModule,
        MatInputModule,
        MatDividerModule,
        MatIconModule,
        MatExpansionModule,
        MatSelectModule,
        MatTableModule,
        MatDialogModule]
  })
  
  export class MaterialModule {
  
  }
