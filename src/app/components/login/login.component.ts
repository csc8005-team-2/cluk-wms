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
    this.session.loginSub = this.session.login(username, password).subscribe(res => {
      this.session.getPermissions().subscribe(_permissions => {
        this.session.permissions = _permissions;
        this.session.setCookie('restaurant', (this.session.permissions.restaurant) ? 'true' : 'false', 90, '/');
        this.session.setCookie('warehouse', (this.session.permissions.warehouse) ? 'true' : 'false', 90, '/');
        this.session.setCookie('driver', (this.session.permissions.driver) ? 'true' : 'false', 90, '/');
        this.session.setCookie('manager', (this.session.permissions.manager) ? 'true' : 'false', 90, '/');

        let prefix = '';
        if (this.session.permissions.manager) {
          this.router.navigate(['/accounts']).then(() => {
            return;
          });
        } else if (this.session.permissions.driver) {
          this.router.navigate(['/driver']).then(() => {
            return;
          });
          return;
        } else {
          if (this.session.permissions.restaurant) {
            prefix = '/restaurant';
          } else if (this.session.permissions.warehouse) {
            prefix = '/warehouse';
          }
          this.router.navigate([prefix + '/total-stock']).then(() => {
            return;
          });
        }
      });

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
