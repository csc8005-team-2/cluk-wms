import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {StaffMember} from '../../classes/staff-member';
import {SessionService} from '../../services/session.service';

@Component({
  selector: 'app-view-permissions',
  templateUrl: './view-permissions.component.html',
  styleUrls: ['./view-permissions.component.css']
})
export class ViewPermissionsComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: StaffMember, private session: SessionService, private dialogRef: MatDialogRef<ViewPermissionsComponent>) { }

  ngOnInit() {
  }

  updatePermissions(warehousePermStr: string, restaurantPermStr: string, driverPermStr: string) {
    const warehousePerm: boolean = (warehousePermStr.toLowerCase() === 'true') ? true : false;
    const restaurantPerm: boolean = (restaurantPermStr.toLowerCase() === 'true') ? true : false;
    const driverPerm: boolean = (driverPermStr.toLowerCase() === 'true') ? true : false;

    this.session.setPermission(this.data.username, restaurantPerm, warehousePerm, driverPerm).subscribe(res => {
      window.alert('Permissions successfully changed!');
      this.dialogRef.close();
    }, err => {
      window.alert('Error encountered when changing permissions! Try again later!');
    });
  }
}
