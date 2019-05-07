import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { NewOrderComponent } from './components/new-order/new-order.component';
import { TotalStockComponent } from './components/warehouse/total-stock/total-stock.component';
import { AddStockComponent } from './components/warehouse/add-stock/add-stock.component';
import { RestaurantOrdersComponent } from './components/warehouse/restaurant-orders/restaurant-orders.component';


const routes: Routes = [
  { path: '', component: LoginComponent},
  { path: 'new-order', component: NewOrderComponent },
  { path: 'warehouse/total-stock', component: TotalStockComponent},
  { path: 'warehouse/add-stock', component: AddStockComponent},
  { path: 'warehouse/restaurant-orders', component: RestaurantOrdersComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
