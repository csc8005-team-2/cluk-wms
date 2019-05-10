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
  public warehouseLocations: Location[];
  public restaurantLocations: Location[];

  constructor(@Inject(MAT_DIALOG_DATA) public data: StaffMember, private session: SessionService, private dialogRef: MatDialogRef<ViewPermissionsComponent>) {
    this.session.getRestaurantLocations().subscribe(res => {
      this.restaurantLocations = res;
    });
    this.session.getWarehouseLocations().subscribe(res => {
      this.warehouseLocations = res;
    });
  }

  ngOnInit() {
  }

  updatePermissions(warehousePerm: boolean, restaurantPerm: boolean, driverPerm: boolean, managerPerm: boolean, location: string) {
    const outputElement = this.data;

    this.session.setPermission(this.data.username, restaurantPerm, warehousePerm, driverPerm, managerPerm).subscribe(res => {
      window.alert('Permissions successfully changed!');

      // build output object for updating the table instantly

      outputElement.driver = driverPerm;
      outputElement.restaurant = restaurantPerm;
      outputElement.warehouse = warehousePerm;
      outputElement.manager = managerPerm;
    }, err => {
      window.alert('Error encountered when changing permissions! Try again later!');
    });

    if (location) {
      this.session.setEmployeeLocation(this.data.username, location).subscribe(res => {
        this.dialogRef.close(outputElement);
      }, err => {
        window.alert('Error encountered when changing location! Try again later!');
      });
    }

    this.dialogRef.close(outputElement);
  }
}
