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

  constructor(@Inject(MAT_DIALOG_DATA) public data: StaffMember, private session: SessionService, private dialogRef: MatDialogRef<ViewPermissionsComponent>) {}

  ngOnInit() {
  }

  updatePermissions(warehousePerm: boolean, restaurantPerm: boolean, driverPerm: boolean) {
    this.session.setPermission(this.data.username, restaurantPerm, warehousePerm, driverPerm).subscribe(res => {
      window.alert('Permissions successfully changed!');

      // build output object for updating the table instantly
      const outputElement = this.data;
      outputElement.driver = driverPerm;
      outputElement.restaurant = restaurantPerm;
      outputElement.warehouse = warehousePerm;

      this.dialogRef.close(outputElement);
    }, err => {
      window.alert('Error encountered when changing permissions! Try again later!');
    });
  }
}
