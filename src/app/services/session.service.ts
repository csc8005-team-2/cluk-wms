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
  private venueAddress: string;

  constructor(private http: HttpClient) { }

  /*
  * Methods for authorization and account management
  * @param {string} username, {string} password
  * @returns {string} idToken
  */ 
  login(username: string, password: string): Observable<IdToken> {
    const reqHeader = new HttpHeaders().append('Content-Type', 'application/json');
    const reqBody = {username, password};

    return this.http.post<IdToken>(this.BACKEND_URL + '/login', reqBody, {headers: reqHeader} ).pipe(
      tap ((res: IdToken) => {
        this.idToken = res.idToken;
      }) /* ,
      catchError(this.handleError<IdToken>('login')) */
    );
  }


  /*
  * Methods for authorization and account management
  * @param none
  * @returns http.get<Message>
  */ 
  logout(): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<Message>(this.BACKEND_URL + '/logout', {headers: reqHeader} ).pipe(
      tap ((res: Message) => {
        this.idToken = null;
      }) /* ,
      catchError(this.handleError<Message>('logout')) */
    );
  }

  /*
  * Methods for authorization and account management
  * @param none
  * @returns http.get<UserPermissions>
  */ 
  checkAccess(): Observable<UserPermissions> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<UserPermissions>(this.BACKEND_URL + '/account/check-access', {headers: reqHeader});
  }

  /*
  * Getter method for idToken
  * @param none
  * @returns {string} idToken
  */ 
  getToken(): string {
    return this.idToken;
  }

  /*
  * Method to check if user is loggedin to the system
  * @param none
  * @returns boolean
  */ 
  isLoggedIn(): boolean {
    if (this.idToken) {return true; }
    return false;
  }

  /*
  * Getter method for venue address
  * @param none
  * @returns {string} VenueAddress
  */ 
  getVenueAddress(): string {
    return this.venueAddress;
  }

  /*
  * Methods for accounts management
  * @param {string} username, {string} password, {string} name.
  * @returns http.post<Message>
  */ 
  addAccount(username: string, password: string, name: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    const reqBody = {username, password, name};

    return this.http.post<Message>(this.BACKEND_URL + '/accounts/add', reqBody, {headers: reqHeader});
  }

  /*
  * Setter method to set user permisisons in system.
  * @param {string} username, {boolean} restaurant, {boolean} warehouse, {boolean} driver
  * @returns http.post<Message>
  */ 
  setPermission(username: string, restaurant: boolean, warehouse: boolean, driver: boolean): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    const reqBody = {username, restaurant, warehouse, driver};
    return this.http.post<Message>(this.BACKEND_URL + '/accounts/set-permission', reqBody, {headers: reqHeader});
  }

  /*
  * Method to delete an user account.
  * @param {string} username
  * @returns http.post<Message>
  */ 
  removeAccount(username: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken).append('username', username);

    return this.http.get<Message>(this.BACKEND_URL + '/accounts/remove', {headers: reqHeader} );
  }

  /*
  * Getter method for staff information
  * @param none
  * @returns http.get<StaffMember[]>
  */ 
  getStaffInfo(): Observable<StaffMember[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<StaffMember[]>(this.BACKEND_URL + '/accounts/info', {headers: reqHeader} );
  }

  /*
  * Getter method for total stock at a restaurant.
  * @param {string} address
  * @returns http.get<StockItem[]>
  */ 
  getTotalStockRest(address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken).append('address', address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/restaurant/get-total-stock', {headers: reqHeader});
  }

  /*
  * Method to receive a stock order request from a restaurant.
  * @param {string} address, {number} orderId
  * @returns http.get<Message>
  */ 
  receiveOrder(address: string, orderId: number): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address)
      .append('orderId', orderId.toString());

    return this.http.get<Message>(this.BACKEND_URL + '/restaurant/receive-order', {headers: reqHeader});
  }

  /*
  * Method for a restuarant to request a custom stock order.
  * @param {string} address, {orderContents} StockItem[]
  * @returns http.post<OrderId>
  */ 
  requestCustomOrder(address: string, orderContents: StockItem[]): Observable<OrderId> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

    return this.http.post<OrderId>(this.BACKEND_URL + '/restaurant/request-order/custom', orderContents, {headers: reqHeader});
  }

  /*
  * Method for a restuarant to request a standard stock order.
  * @param {string} address
  * @returns http.post<OrderId>
  */ 
  requestStandardOrder(address: string): Observable<OrderId> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

    return this.http.get<OrderId>(this.BACKEND_URL + '/restaurant/request-order', {headers: reqHeader});
  }

  /*
  * Method to check the minimum stock at a restaurant
  * @param {string} address
  * @returns http.get<StockItem[]>
  */ 
  minStockCheckRest(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/restaurant/min-stock-check', {headers: reqHeader});
  }

  /*
  * Method to change the minimum stock level threshold at a restaurant
  * @param {string} address, {StockItem} newStockLvl
  * @returns http.post<Message>
  */ 
  updateMinStockRest(_address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/restaurant/update-min-stock', newStockLvl, {headers: reqHeader});
  }

  /*
  * Method to create a meal at the restaurant
  * @param {string} address, {string} meal
  * @returns http.post<Message>
  */ 
  createMeal(_address: string, _meal: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<Message>(this.BACKEND_URL + '/restaurant/create-meal', {headers: reqHeader});
  }

  /*
  * Method to update the stock level at a restaurant
  * @param {string} address, {StockItem} newStockLvl
  * @returns http.post<Message>
  */ 
  updateStockRest(_address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/restaurant/update-stock', newStockLvl, {headers: reqHeader});
  }

  /*
  * Getter method for the price of a restaurant meal
  * @param {string} _meal
  * @returns http.get<MealPrice>
  */ 
  getPrice(_meal: string): Observable<MealPrice> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('meal', _meal);

    return this.http.get<MealPrice>(this.BACKEND_URL + '/restaurant/get-price', {headers: reqHeader});
  }

  // methods for handling warehouse management

  /*
  * Getter method for the total stock held at the central warehouse
  * @param {string} _address
  * @returns http.get<StockItem[]>
  */ 
  getTotalStockWar(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/get-total-stock', {headers: reqHeader});
  }

  /*
  * Method to update the stock level at a warehouse
  * @param {string} address, {StockItem} newStockLvl
  * @returns http.post<Message>
  */ 
  updateStockWar(_address: string, newStockLvls: StockItem[]): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/warehouse/update-stock', newStockLvls, {headers: reqHeader});
  }

  /*
  * Method to approve and dispatch a stock order request to a restaurant
  * @param {string} address, {number} orderId
  * @returns http.get<Message>
  */ 
  sendOrder(_address: string, orderId: number): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address)
      .append('orderId', orderId.toString());

    return this.http.get<Message>(this.BACKEND_URL + '/warehouse/send-order', {headers: reqHeader});
  }

  /*
  * Getter method for the minimum stock at a warehouse
  * @param {string} address
  * @returns http.get<StockItem[]>
  */ 
  getMinStockWar(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/get-min-stock', {headers: reqHeader});
  }

  /*
  * Method to check the minimum stock threshold at a warehouse
  * @param {string} address
  * @returns http.get<StockItem[]>
  */ 
  minStockCheckWar(_address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/min-stock-check', {headers: reqHeader});
  }

  /*
  * Method to change the minimum stock threshold at a warehouse
  * @param {string} address, {newStockLvl} StockItem.
  * @returns http.post<Message>
  */ 
  updateMinStockWar(_address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.post<Message>(this.BACKEND_URL + '/warehouse/update-min-stock', newStockLvl, {headers: reqHeader});
  }

  /*
  * Method to assign a delivery order to a driver
  * @param {number} _orderId, {number} _driverId.
  * @returns http.post<Message>
  */ 
  assignToDriver(_orderId: number, _driverId: number): Observable<Message> {
    const orderId = _orderId.toString();
    const driverId = _driverId.toString();

    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('orderId', orderId)
      .append('driverId', driverId);

    return this.http.get<Message>(this.BACKEND_URL + '/warehouse/assign-to-driver', {headers: reqHeader});
  }

  /*
  * Getter method for the pending stock requests
  * @param none
  * @returns http.get<OrderEntry[]>
  */ 
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
