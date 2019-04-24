import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { NewOrderComponent } from './components/new-order/new-order.component';
import { TotalStockComponent } from './components/warehouse/total-stock/total-stock.component';
import { AddStockComponent } from './components/warehouse/add-stock/add-stock.component';
import { RestaurantOrdersComponent } from './components/warehouse/restaurant-orders/restaurant-orders.component';
import { WarehouseManagerComponent } from './components/warehouse/warehouse-manager/warehouse-manager.component';
import { WarehouseStockRemoveComponent } from './components/warehouse/warehouse-stock-remove/warehouse-stock-remove.component';
import { QueryStockComponent } from './components/restaurant/query-stock/query-stock.component';
import { ReceiveStockComponent } from './components/restaurant/receive-stock/receive-stock.component';
import { OrderStockComponent } from './components/restaurant/order-stock/order-stock.component';
import { RestaurantManagerComponent } from './components/restaurant/restaurant-manager/restaurant-manager.component';
import { RestaurantStockRemoveComponent } from './components/restaurant/restaurant-stock-remove/restaurant-stock-remove.component';



const routes: Routes = [
  { path: '', component: LoginComponent},
  { path: 'new-order', component: NewOrderComponent },
  { path: 'total-stock', component: TotalStockComponent},
  { path: 'add-stock', component: AddStockComponent},
  { path: 'restaurant-orders', component: RestaurantOrdersComponent},
  { path: 'warehouse-manager', component: WarehouseManagerComponent},
  { path: 'warehouse-stock-remove', component: WarehouseStockRemoveComponent},
  { path: 'query-stock', component: QueryStockComponent},
  { path: 'receive-stock', component: ReceiveStockComponent},
  { path: 'order-stock', component: OrderStockComponent},
  { path: 'restaurant-manager', component: RestaurantManagerComponent},
  { path: 'restaurant-stock-remove', component: RestaurantStockRemoveComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
