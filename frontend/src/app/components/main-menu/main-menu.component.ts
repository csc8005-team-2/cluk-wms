import { Component, OnInit } from '@angular/core';
import { SessionService } from 'src/app/services/session.service';
import { Router } from '@angular/router';
import {UserPermissions} from '../../classes/user-permissions';

@Component({
  selector: 'app-main-menu',
  templateUrl: './main-menu.component.html',
  styleUrls: ['./main-menu.component.css']
})
export class MainMenuComponent implements OnInit {

  constructor(private session: SessionService, private router: Router) {
  }

  logout() {
    this.session.logout().subscribe(res => {
      this.router.navigate(['']);
    });
  }

  isLoggedIn(): boolean {
    return this.session.isLoggedIn();
  }

  checkPermissions() {
    return this.session.permissions;
  }

  
  ngOnInit() {
  }

}
