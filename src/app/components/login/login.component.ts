import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private http: HttpClient) { }

  login(_username: string, _password: string) {
    const reqHeader = new HttpHeaders().append('Content-Type', 'application/json');
    const reqBody = {username: _username, password: _password};
    this.http.post('http://localhost:9998/login', reqBody, {headers: reqHeader, responseType: 'text'} ).subscribe((res) => {
      console.log(res);
  }, err => {
    console.log(err);
  });

  }

  ngOnInit() {
  }

}
