import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MatDialog, MatTableDataSource} from '@angular/material';
import {SessionService} from '../../services/session.service';
import {StaffMember} from '../../classes/staff-member';
import {CreateAccountComponent} from '../create-account/create-account.component';
import {ViewPermissionsComponent} from '../view-permissions/view-permissions.component';

@Component({
  selector: 'app-account-manager',
  templateUrl: './account-manager.component.html',
  styleUrls: ['./account-manager.component.css']
})
export class AccountManagerComponent implements OnInit {
  // Stock Object table
  staffList: MatTableDataSource<StaffMember>;
  staffListSub: any;

  // displayed columns format
  displayedColumns: string[] = ['name', 'login', 'permissions', 'delete'];

  applyFilter(filterValue: string) {
    this.staffList.filter = filterValue.trim().toLowerCase();
  }

  constructor(private cdRef: ChangeDetectorRef, private session: SessionService, private dialog: MatDialog) {
    this.staffListSub = this.session.getStaffInfo().subscribe(res => {
      this.staffList = new MatTableDataSource(res);
    }, err => {
      console.log(err);
    });
  }

  deleteAccount(user: StaffMember) {
    this.session.removeAccount(user.username).subscribe(res => {
      window.alert('Account ' + user.username + ' successfully removed!');
      // refresh table
      const staffListArr = this.staffList.data;
      staffListArr.splice(staffListArr.indexOf(user), 1);
      this.staffList = new MatTableDataSource(staffListArr);
      this.cdRef.detectChanges();

    }, err => {
      window.alert('Error encountered when removing account! No changes made.');
    });
  }

  addAccount() {
    const dialogRef = this.dialog.open(CreateAccountComponent, {
      width: '300px'
    });

    dialogRef.afterClosed().subscribe(accCreated => {
      if (accCreated) {
        this.staffListSub.unsubscribe();
        this.staffListSub = this.session.getStaffInfo().subscribe(res => {
          this.staffList = new MatTableDataSource(res);
        }, err => {
          console.log(err);
        });
      }
    });
  }

  viewPermissions(element: StaffMember) {
    const dialogRef = this.dialog.open(ViewPermissionsComponent, {
      width: '300px',
      data: element
    });

    dialogRef.afterClosed().subscribe(newEntry => {
      // update table
      const staffListArray = this.staffList.data;
      staffListArray[staffListArray.indexOf(element)] = newEntry;
      this.staffList = new MatTableDataSource(staffListArray);
      this.cdRef.detectChanges();
    });
  }

  ngOnInit() {
  }

}
