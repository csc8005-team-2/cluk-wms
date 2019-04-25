import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, throwError, of, Subject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { IdToken } from '../classes/id-token';
import { Message } from '../classes/message';
import {StaffMember} from '../classes/staff-member';
import {UserPermissions} from '../classes/user-permissions';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private BACKEND_URL = 'http://localhost:9998';
  private idToken: string;

  constructor(private http: HttpClient) { }

  // methods for authorization
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

  // call for stock available to order list
  // call for stock available at restaurant list
  // call for submitting the order
  // call for deducting stock
  
  /* private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
   
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
   
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  } */
}
