import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, throwError, of, Subject } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { IdToken } from '../classes/id-token';
import { Message } from '../classes/message';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private idToken: string;

  constructor(private http: HttpClient) { }

  login(_username: string, _password: string): Observable<IdToken> {
    const reqHeader = new HttpHeaders().append('Content-Type', 'application/json');
    const reqBody = {username: _username, password: _password};

    return this.http.post<IdToken>('http://localhost:9998/login', reqBody, {headers: reqHeader} ).pipe(
      tap ((res: IdToken) => {
        this.idToken = res.idToken;
      }) /* ,
      catchError(this.handleError<IdToken>('login')) */
    );
  }

  logout(): Observable<Message> {
    const reqHeader = new HttpHeaders().append('Authorization', this.idToken);

    return this.http.get<Message>('http://localhost:9998/logout', {headers: reqHeader} ).pipe(
      tap ((res: Message) => {
        this.idToken = null;
      }),
      catchError(this.handleError<Message>('logout'))
    );
  }

  getToken(): string {
    return this.idToken;
  }

  isLoggedIn(): boolean {
    if (this.idToken) return true;
    return false;
  }

  // call for stock available to order list
  // call for stock available at restaurant list
  // call for submitting the order
  // call for deducting stock
  
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
   
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
   
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
