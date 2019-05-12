import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
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
import {AccountManagerComponent} from './components/account-manager/account-manager.component';
import {DriverComponent} from './components/driver/driver.component';
import {POSComponent} from './components/restaurant/pos/pos.component';



const routes: Routes = [
  { path: '', component: LoginComponent},
  { path: 'warehouse/warehouse-manager', component: WarehouseManagerComponent},
  { path: 'warehouse/warehouse-stock-remove', component: WarehouseStockRemoveComponent},
  { path: 'restaurant/total-stock', component: QueryStockComponent},
  { path: 'restaurant/receive-stock', component: ReceiveStockComponent},
  { path: 'restaurant/order-stock', component: OrderStockComponent},
  { path: 'restaurant/restaurant-manager', component: RestaurantManagerComponent},
  { path: 'restaurant/restaurant-stock-remove', component: RestaurantStockRemoveComponent},
  { path: 'warehouse/total-stock', component: TotalStockComponent},
  { path: 'warehouse/add-stock', component: AddStockComponent},
  { path: 'warehouse/restaurant-orders', component: RestaurantOrdersComponent},
  { path: 'accounts', component: AccountManagerComponent},
  { path: 'driver', component: DriverComponent},
  { path: 'restaurant/sales', component: POSComponent}
  ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
