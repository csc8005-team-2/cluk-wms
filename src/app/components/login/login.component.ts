import {Component, OnInit} from '@angular/core';
import {SessionService} from "../../services/session.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private wrongCredentials = false;

  constructor(private session: SessionService) { }

  ngOnInit() {
  }

  login(_username: string, _password: string) {
    this.wrongCredentials = false;
    this.session.login(_username, _password).subscribe(res => {

    }, err => {
      if (err.error === 'WRONG_CREDENTIALS')
        this.wrongCredentials = true;
    });
  }

  
  areCredentialsWrong(): boolean {
    return this.wrongCredentials;
  }
}
