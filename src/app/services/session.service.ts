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
import {StockName} from '../classes/stock-name';
import {ComparativeStockItem} from '../classes/comparative-stock-item';
import {VenueLocation} from '../classes/venue-location';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private BACKEND_URL = 'http://api.chickenlovers.ml';
  private idToken = '';
  private venueAddress: string;
  public permissions = {restaurant: false, warehouse: false, driver: false, manager: false};
  public loginSub: any;

  constructor(private http: HttpClient) {
    // check for cookies to avoid mess on refreshing
    this.idToken = this.getCookie('token');
    this.venueAddress = this.getCookie('address');

    const restPerm = (this.getCookie('restaurant')) ? ((this.getCookie('restaurant') === 'true')) : false;
    const warPerm = (this.getCookie('warehouse')) ? ((this.getCookie('warehouse') === 'true')) : false;
    const drivPerm = (this.getCookie('driver')) ? ((this.getCookie('driver') === 'true')) : false;
    const manPerm = (this.getCookie('manager')) ? ((this.getCookie('manager') === 'true')) : false;

    this.permissions = {restaurant: restPerm, warehouse: warPerm, driver: drivPerm, manager: manPerm};
  }

  // methods for storing cookies.. yummy!
  public getCookie(name: string) {
    const ca: Array<string> = document.cookie.split(';');
    const caLen: number = ca.length;
    const cookieName = `${name}=`;
    let c: string;

    for (let i = 0; i < caLen; i += 1) {
      c = ca[i].replace(/^\s+/g, '');
      if (c.indexOf(cookieName) === 0) {
        return c.substring(cookieName.length, c.length);
      }
    }
    return '';
  }

  public deleteCookie(name, path) {
    this.setCookie(name, '', -1, path);
  }

  public setCookie(name: string, value: string, expireDays: number, path: string = '') {
    const d: Date = new Date();
    d.setTime(d.getTime() + expireDays * 24 * 60 * 60 * 1000);
    const expires = `expires=${d.toUTCString()}`;
    const cookiePath = path ? `; path=${path}` : '';
    document.cookie = `${name}=${value}; ${expires}${cookiePath}`;
  }

  /*
  * Methods for authorization and account management
  * @param {string} username, {string} password
  * @returns {string} idToken
  */ 
  login(username: string, password: string): Observable<IdToken> {
    const reqHeader = new HttpHeaders().append('Content-Type', 'application/json');
    const reqBody = {username, password};

    return this.http.post<IdToken>(this.BACKEND_URL + '/login', reqBody, {headers: reqHeader} ).pipe(
      tap ((token: IdToken) => {
        this.idToken = token.idToken;
        this.venueAddress = token.location;
        // set cookies for better refresh
        this.setCookie('token', this.idToken, 90, '/');
        this.setCookie('address', this.venueAddress, 90, '/');
      }) /* ,
      catchError(this.handleError<IdToken>('login')) */
    );
  }

  getPermissions(): Observable<UserPermissions> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<UserPermissions>(this.BACKEND_URL + '/accounts/check-access', {headers: reqHeader} );
  }

  getStockNames(): Observable<StockName[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<StockName[]>(this.BACKEND_URL + '/warehouse/get-stock-names', {headers: reqHeader} );
  }
  /*
* Methods for authorization and account management
* @param none
* @returns http.get<Message>
*/
  logout(): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    // Clean cookies. Even if remote request will fail, local machine is secure
    this.deleteCookie('token', '/');
    this.deleteCookie('address', '/');
    this.deleteCookie('restaurant', '/');
    this.deleteCookie('warehouse', '/');
    this.deleteCookie('driver', '/');
    this.deleteCookie('manager', '/');

    return this.http.get<Message>(this.BACKEND_URL + '/logout', {headers: reqHeader} ).pipe(
      tap ((res: Message) => {
        this.idToken = '';
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

  // TO DO: finish when backend fixed
  retrieveDirections(): Observable<VenueLocation[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<VenueLocation[]>(this.BACKEND_URL + '/driver/plot-route', {headers: reqHeader});
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
  * Method to check if user is logged in to the system
  * @param none
  * @returns boolean
  */ 
  isLoggedIn(): boolean {
    if (this.idToken === '') {return false; }
    return true;
  }

  /*
  * Getter method for venue address
  * @param none
  * @returns {string} VenueAddress
  */ 
  getVenueAddress(): string {
    return this.venueAddress;
  }

  getWarehouseLocations(): Observable<Location[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<Location[]>(this.BACKEND_URL + '/warehouse/get-list', {headers: reqHeader});
  }

  getRestaurantLocations(): Observable<Location[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<Location[]>(this.BACKEND_URL + '/restaurant/get-list', {headers: reqHeader});
  }

  setEmployeeLocation(username: string, address: string): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    const reqBody = {username, address};

    return this.http.post<Message>(this.BACKEND_URL + '/accounts/set-work-location', reqBody, {headers: reqHeader});
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
  setPermission(username: string, restaurant: boolean, warehouse: boolean, driver: boolean, manager: boolean): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);
    const reqBody = {username, restaurant, warehouse, driver, manager};
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
  getTotalStockRest(address: string): Observable<ComparativeStockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken).append('address', address);

    return this.http.get<ComparativeStockItem[]>(this.BACKEND_URL + '/restaurant/get-total-stock', {headers: reqHeader});
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
      .append('address', address).append('custom', 'true');

    return this.http.post<OrderId>(this.BACKEND_URL + '/restaurant/request-order/custom', orderContents, {headers: reqHeader});
  }

  /*
  * Method for a restuarant to request a standard stock order.
  * @param {string} address
  * @returns http.post<OrderId>
  */ 
  requestStandardOrder(address: string): Observable<OrderId> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address).append('custom', 'false');

    return this.http.get<OrderId>(this.BACKEND_URL + '/restaurant/request-order', {headers: reqHeader});
  }

  /*
  * Method to check the minimum stock at a restaurant
  * @param {string} address
  * @returns http.get<StockItem[]>
  */ 
  minStockCheckRest(address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

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

  getMealNames(): Observable<MealPrice[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<MealPrice[]>(this.BACKEND_URL + '/restaurant/get-meals-names', {headers: reqHeader});
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
  updateStockRest(address: string, newStockLvl: StockItem[]): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

    return this.http.post<Message>(this.BACKEND_URL + '/restaurant/update-stock', newStockLvl, {headers: reqHeader});
  }

  /*
  * Getter method for the price of a restaurant meal
  * @param {string} _meal
  * @returns http.get<MealPrice>
  */ 
  getPrice(meal: string): Observable<MealPrice> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('meal', meal);

    return this.http.get<MealPrice>(this.BACKEND_URL + '/restaurant/get-price', {headers: reqHeader});
  }

  // methods for handling warehouse management

  /*
  * Getter method for the total stock held at the central warehouse
  * @param {string} _address
  * @returns http.get<StockItem[]>
  */ 
  getTotalStockWar(_address: string): Observable<ComparativeStockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address);

    return this.http.get<ComparativeStockItem[]>(this.BACKEND_URL + '/warehouse/get-total-stock', {headers: reqHeader});
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

  approveOrder(_address: string, orderId: number): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', _address)
      .append('orderId', orderId.toString());

    return this.http.get<Message>(this.BACKEND_URL + '/warehouse/approve-order', {headers: reqHeader});
  }

  /*
  * Method to decline a stock order request from a restaurant
  * @param {string} address, {number} orderId
  * @returns http.get<Message>
  */
  declineOrder(orderId: number): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('orderId', orderId.toString());

    return this.http.get<Message>(this.BACKEND_URL + '/warehouse/decline-order', {headers: reqHeader});
  }

  /*
  * Getter method for the minimum stock at a warehouse
  * @param {string} address
  * @returns http.get<StockItem[]>
  */
  getMinStockWar(address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/get-min-stock', {headers: reqHeader});
  }

  /*
  * Getter method for the minimum stock at a warehouse
  * @param {string} address
  * @returns http.get<StockItem[]>
  */
  getMinStockRest(address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/restaurant/get-min-stock', {headers: reqHeader});
  }

  /*
  * Method to check the minimum stock threshold at a warehouse
  * @param {string} address
  * @returns http.get<StockItem[]>
  */ 
  minStockCheckWar(address: string): Observable<StockItem[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

    return this.http.get<StockItem[]>(this.BACKEND_URL + '/warehouse/min-stock-check', {headers: reqHeader});
  }

  /*
  * Method to change the minimum stock threshold at a warehouse
  * @param {string} address, {newStockLvl} StockItem.
  * @returns http.post<Message>
  */ 
  updateMinStockWar(address: string, newStockLvl: StockItem): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken)
      .append('address', address);

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

  getTodaysOrders(): Observable<OrderEntry[]> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken).append('address', this.venueAddress);

    return this.http.get<OrderEntry[]>(this.BACKEND_URL + '/restaurant/get-todays-orders', {headers: reqHeader});
  }

  /* setOrderView(orderContents: OrderEntry[]) {
    this.viewedOrder = orderContents;
  }

  getOrderView(): OrderEntry[] {
    return this.viewedOrder;
  } */

  /* private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
   
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
   
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  } */
}
