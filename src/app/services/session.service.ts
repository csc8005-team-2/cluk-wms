import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, throwError, of, Subject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { IdToken } from '../classes/id-token';
import { Message } from '../classes/message';
import {StaffMember} from '../classes/staff-member';
import {UserPermissions} from '../classes/user-permissions';
import {StockItem} from '../classes/stock-item';
import {OrderId} from '../classes/order-id';
import {MealPrice} from '../classes/meal-price';
import {OrderEntry} from '../classes/order-entry';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private BACKEND_URL = 'http://localhost:9998';
  private idToken: string;

  constructor(private http: HttpClient) { }

  // methods for authorization and account management
  login(_username: string, _password: string): Observable<IdToken> {
    const reqHeader = new HttpHeaders().append('Content-Type', 'application/json');
    const reqBody = {username: _username, password: _password};

    return this.http.post<IdToken>(this.BACKEND_URL + '/login', reqBody, {headers: reqHeader} ).pipe(
      tap ((res: IdToken) => {
        this.idToken = res.idToken;
      }) /* ,
      catchError(this.handleError<IdToken>('login')) */
    );
  }

  logout(): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<Message>(this.BACKEND_URL + '/logout', {headers: reqHeader} ).pipe(
      tap ((res: Message) => {
        this.idToken = null;
      }) /* ,
      catchError(this.handleError<Message>('logout')) */
    );
  }

  checkAccess(): Observable<UserPermissions> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<UserPermissions>(this.BACKEND_URL + '/account/check-access', {headers: reqHeader});
  }

  getToken(): string {
    return this.idToken;
  }

  isLoggedIn(): boolean {
    if (this.idToken) {return true; }
    return false;
  }

  // methods for accounts management
  addAccount(_username: string, _password: string, _name: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    const reqBody = {username: _username, password: _password, name: _name}

    return this.http.post<Message>(this.BACKEND_URL + '/accounts/add', reqBody, {headers: reqHeader});
  }

  setPermission(_username: string, _restaurant: boolean, _warehouse: boolean, _driver: boolean): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    const reqBody = {username: _username, restaurant: _restaurant, warehouse: _warehouse, driver: _driver}
    return this.http.post<Message>(this.BACKEND_URL + '/accounts/set-permission', reqBody, {headers: reqHeader});
  }

  removeAccount(_username: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken).append('username', _username);

    return this.http.get<Message>(this.BACKEND_URL + '/accounts/remove', {headers: reqHeader} );
  }

  getStaffInfo(): Observable<StaffMember[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    
    return this.http.get<StaffMember[]>(this.BACKEND_URL + '/accounts/info', {headers: reqHeader} );
  }

  // methods for restaurant stock management
  getTotalStockRest(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken).append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/restaurant/get-total-stock', {headers: reqHeader});
  }

  receiveOrder(_address: string, _orderId: number): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address)
      .append('orderId', _orderId.toString());

    return this.http.get<Message>(this.BACKEND_URL + '/restaurant/receive-order', {headers: reqHeader});
  }

  requestCustomOrder(_address: string, _orderContents: StockItem[]): Observable<OrderId> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<OrderId>(this.BACKEND_URL + '/restaurant/request-order/custom', _orderContents, {headers: reqHeader});
  }

  requestStandardOrder(_address: string): Observable<OrderId> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<OrderId>(this.BACKEND_URL + '/restaurant/request-order', {headers: reqHeader});
  }


  minStockCheckRest(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/restaurant/min-stock-check', {headers: reqHeader});
  }

  updateMinStockRest(_address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/restaurant/update-min-stock', newStockLvl, {headers: reqHeader});
  }

  createMeal(_address: string, _meal: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<Message>(this.BACKEND_URL + '/restaurant/create-meal', {headers: reqHeader});
  }

  updateStockRest(_address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/restaurant/update-stock', newStockLvl, {headers: reqHeader});
  }

  getPrice(_meal: string): Observable<MealPrice> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('meal', _meal);

    return this.http.get<MealPrice>(this.BACKEND_URL + '/restaurant/get-price', {headers: reqHeader});
  }

  // methods for handling warehouse management
  getTotalStockWar(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/get-total-stock', {headers: reqHeader});
  }

  updateStockWar(_address: string, newStockLvls: StockItem[]): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/warehouse/update-stock', newStockLvls, {headers: reqHeader});
  }

  sendOrder(_address: string, orderId: number): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address)
      .append('orderId', orderId.toString());

    return this.http.get<Message>(this.BACKEND_URL + '/warehouse/send-order', {headers: reqHeader});
  }

  getMinStockWar(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/get-min-stock', {headers: reqHeader});
  }

  minStockCheckWar(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/min-stock-check', {headers: reqHeader});
  }

  updateMinStockWar(_address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/warehouse/update-min-stock', newStockLvl, {headers: reqHeader});
  }

  assignToDriver(_orderId: number, _driverId: number): Observable<Message> {
    const orderId = _orderId.toString();
    const driverId = _driverId.toString();

    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('orderId', orderId)
      .append('driverId', driverId);

    return this.http.get<Message>(this.BACKEND_URL + '/warehouse/assign-to-driver', {headers: reqHeader});
  }

  getPendingOrders(): Observable<OrderEntry[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<OrderEntry[]>(this.BACKEND_URL + '/warehouse/get-pending-orders', {headers: reqHeader});
  }

  /* private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
   
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
   
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  } */
}
