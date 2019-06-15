import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {GMapService} from '../../services/gmap.service';
import {SessionService} from '../../services/session.service';
import {VenueLocation} from '../../classes/venue-location';

@Component({
  selector: 'app-driver',
  templateUrl: './driver.component.html',
  styleUrls: ['./driver.component.css']
})
export class DriverComponent implements OnInit {

  @ViewChild('map') mapRef: ElementRef;
  @ViewChild('directionsPanel') dirPanelRef: ElementRef;
  private map: google.maps.Map;

  private wareHouse = {lat: 54.8761706, lng: -1.588353};
  private restaurant1 = {lat: 55.0673382, lng: -1.6344661};
  private restaurant2 = {lat: 55.4124929, lng: -1.7090826};
  private restaurant3 = {lat: 54.6118847, lng: -1.580482};
  private restaurant4 = {lat: 54.2320962, lng: -1.3433682};
  private restaurant5 = {lat: 54.4852063, lng: -0.6150377};
  private directionsService: google.maps.DirectionsService;
  private directionsDisplay: google.maps.DirectionsRenderer;

  private destinations: VenueLocation[];

  constructor(private gmapService: GMapService, private session: SessionService) {
  }

  ngOnInit() {

    this.gmapService.initMap(this.mapRef.nativeElement, {
      zoom: 9,
      center: {lat:  54.8761706, lng:  -1.588353}
    }).then(() => {
      this.map = this.gmapService.getMap();

      const markerWh = this.gmapService.addPin(this.wareHouse);
      const markerR1 = this.gmapService.addPin(this.restaurant1);
      const markerR2 = this.gmapService.addPin(this.restaurant2);
      const markerR3 = this.gmapService.addPin(this.restaurant3);
      const markerR4 = this.gmapService.addPin(this.restaurant4);
      const markerR5 = this.gmapService.addPin(this.restaurant5);

      const infowindowWH = new google.maps.InfoWindow({// label
        content: 'Warehouse'
      });
      /* const infowindowR1 = new google.maps.InfoWindow({
        content: 'restaurants'
      }); */
      infowindowWH.open(this.map, markerWh);
      // infowindowR1.open(this.map, markerR1);
      this.directionsDisplay = new google.maps.DirectionsRenderer();
      this.directionsDisplay.setMap(this.map);

      this.directionsService = new google.maps.DirectionsService();

      this.session.retrieveDirections().subscribe(res => {
        this.destinations = res;
        this.getDirections(this.wareHouse, this.wareHouse, this.destinations);
      });
    });
  }

  getDirections(origin: any, destination: any, waypoints: VenueLocation[]) {
    const directionWaypoints = [];

    for (let i = 0; i < waypoints.length; i++) {
      directionWaypoints.push({location: waypoints[i].address, stopover: true});
    }

    this.directionsService.route({
      origin,
      destination,
      waypoints: directionWaypoints,
      optimizeWaypoints: true,
      travelMode: google.maps.TravelMode.DRIVING
    }, (response, status) => {
      if (status === google.maps.DirectionsStatus.OK) {
        this.directionsDisplay.setDirections(response);

        const route = response.routes[0];
        let totalDistance = 0;
        let totalDuration = 0;
        
        let tripSummary = '';
        for (let i = 0; i < route.legs.length - 1; i++) {
          const routeSegment = i + 1;
          tripSummary += '<b>delivery destination: ' + routeSegment +
            '</b><br>';
          tripSummary += route.legs[i].end_address + '<br>';
          tripSummary += route.legs[i].distance.text + '<br><br>';
          tripSummary += route.legs[i].duration.text + '<br><br>';
          totalDistance = totalDistance + route.legs[i].distance.value;
          totalDuration = totalDuration + route.legs[i].duration.value;
        }

        if (route.legs.length > 1) {
          tripSummary += '<b>return to warehouse:</b><br>';
          tripSummary += route.legs[route.legs.length - 1].end_address + '<br>';
          tripSummary += route.legs[route.legs.length - 1].distance.text + '<br><br>';
          tripSummary += route.legs[route.legs.length - 1].duration.text + '<br><br>';
          totalDistance = totalDistance + route.legs[route.legs.length - 1].distance.value;
          totalDuration = totalDuration + route.legs[route.legs.length - 1].duration.value;
        }
        // convert distance to kilometers
        totalDistance = totalDistance / 1000;

        // TO DO: add 45 minutes if total duration more than 4 hours
        let breakAdded = false;
        if (totalDuration > 4 * 3600) {
          totalDuration += 45 * 60;
          breakAdded = true;
        }

        // convert duration into more bearable format
        const totalDurationHrs = totalDuration / 3600;
        const totalDurationMin = (totalDuration / 60) % 60;

        tripSummary += 'The total distance will be ' + totalDistance.toFixed(1) + ' kilometers ';
        tripSummary += 'The total duration will be ' + ((totalDurationHrs >= 1) ? (totalDurationHrs.toFixed(0) + ' hours ') : '')  + totalDurationMin.toFixed(0) + ' minutes ';
        if (breakAdded) {
          tripSummary += 'Total duration of the trip includes 45 minute break.';
        }
        this.dirPanelRef.nativeElement.innerHTML = tripSummary;

      } else {
        window.alert('Directions request failed due to ' + status);
      }
    });
  }
}
