import { Component, OnInit } from '@angular/core';
import {SessionService} from '../../../services/session.service';
import {Router} from '@angular/router';
@Component({
  selector: 'app-receive-stock',
  templateUrl: './receive-stock.component.html',
  styleUrls: ['./receive-stock.component.css']
})
export class ReceiveStockComponent implements OnInit {
  
  constructor(private session: SessionService, private router: Router) { }

  confirmReceipt(orderId: number) {
    this.session.receiveOrder(this.session.getVenueAddress(), orderId).subscribe(res => {
      window.alert('Order ' + orderId + ' successfully received. Stock amounts updated in the database.');
      this.router.navigate(['']);
    }, err => {
      console.log(err);
    });
  }

  ngOnInit() {
  }

}


