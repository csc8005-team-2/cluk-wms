import {Component, OnInit} from '@angular/core';
import {SessionService} from '../../services/session.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private wrongCredentials = false;

  constructor(private session: SessionService, private router: Router) { }

  ngOnInit() {
  }

  login(username: string, password: string) {
    this.wrongCredentials = false;
    this.session.login(username, password).subscribe(res => {
      let prefix = '';
      if (this.session.permissions.manager) {
        this.router.navigate(['accounts']);
        return;
      } else if (this.session.permissions.driver) {
        this.router.navigate(['driver']);
        return;
      } else {
        if (this.session.permissions.restaurant) {
          prefix = 'restuarant';
        } else if (this.session.permissions.warehouse) {
          prefix = 'warehouse';
        }
        this.router.navigate([prefix + '/total-stock']);
      }
    }, err => {
      if (err.error === 'WRONG_CREDENTIALS') {
        this.wrongCredentials = true;
      }
    });
  }

  isLoggedIn(): boolean {
    return this.session.isLoggedIn();
  }
  
  areCredentialsWrong(): boolean {
    return this.wrongCredentials;
  }
}
