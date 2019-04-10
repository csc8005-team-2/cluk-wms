import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { NewOrderComponent } from './components/new-order/new-order.component';
import { TotalStockComponent } from './components/warehouse/total-stock/total-stock.component';

const routes: Routes = [
  { path: '', component: LoginComponent},
  { path: 'new-order', component: NewOrderComponent },
  { path: 'total-stock', component: TotalStockComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
