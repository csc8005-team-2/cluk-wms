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
      this.router.navigate(['total-stock']);
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
