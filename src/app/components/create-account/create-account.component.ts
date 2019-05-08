import { Component, OnInit } from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {SessionService} from '../../services/session.service';

@Component({
  selector: 'app-create-account',
  templateUrl: './create-account.component.html',
  styleUrls: ['./create-account.component.css']
})
export class CreateAccountComponent implements OnInit {

  constructor(private dialogRef: MatDialogRef<CreateAccountComponent>, private session: SessionService) { }

  ngOnInit() {
  }

  submitAccountData(name: string, username: string, password: string) {
    this.session.addAccount(username, password, name).subscribe(res => {
      window.alert('Account successfully created!');
      this.dialogRef.close();
    }, err => {
      window.alert('Error when creating an account! Please try again later');
    });
  }

}
