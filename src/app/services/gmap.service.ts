import { Injectable } from '@angular/core';

const getScriptSrc = (callbackName) => {
  return `https://maps.googleapis.com/maps/api/js?key=AIzaSyCj-g_yjS-u4g0Qr-xIjHF7gD9DlfnUboE&callback=${callbackName}`;
}

@Injectable({
  providedIn: 'root'
})

export class GMapService {
  private map: google.maps.Map;
  private geocoder: google.maps.Geocoder;
  private scriptLoadingPromise: Promise<void>;

  constructor() {
    //Loading script
    this.loadScriptLoadingPromise();
    //Loading other components
    this.onReady().then(() => {
      this.geocoder = new google.maps.Geocoder();
    });
  }

  onReady(): Promise<void> {
    return this.scriptLoadingPromise;
  }

  initMap(mapHtmlElement: HTMLElement, options: google.maps.MapOptions): Promise<google.maps.Map> {
    return this.onReady().then(() => {
      return this.map = new google.maps.Map(mapHtmlElement, options);
    });
  }

  getMap(): google.maps.Map {
    return this.map;
  }

  addPin(position) {
    return new google.maps.Marker
    ({                                                                                          //MARKER
      position,
      icon: {
        path: google.maps.SymbolPath.CIRCLE,
        scale: 5
      },
      animation: google.maps.Animation.DROP,
      map: this.map,
    });
  }

  private loadScriptLoadingPromise() {
    const script = window.document.createElement('script');
    script.type = 'text/javascript';
    script.async = true;
    script.defer = true;
    const callbackName: string = 'UNIQUE_NAME_HERE';
    script.src = getScriptSrc(callbackName);
    this.scriptLoadingPromise = new Promise<void>((resolve: Function, reject: Function) => {
      (<any>window)[callbackName] = () => { resolve(); };

      script.onerror = (error: Event) => { reject(error); };
    });
    window.document.body.appendChild(script);
  }

}
