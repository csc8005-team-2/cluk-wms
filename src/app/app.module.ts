import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { HttpClientModule } from '@angular/common/http';

import { MaterialModule } from './material.module'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { NewOrderComponent } from './components/new-order/new-order.component';
import { MainMenuComponent } from './components/main-menu/main-menu.component';
import { TotalStockComponent } from './components/warehouse/total-stock/total-stock.component';
import { AddStockComponent } from './components/warehouse/add-stock/add-stock.component';
import { RestaurantOrdersComponent } from './components/warehouse/restaurant-orders/restaurant-orders.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NewOrderComponent,
    MainMenuComponent,
    TotalStockComponent,
    AddStockComponent,
    RestaurantOrdersComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
