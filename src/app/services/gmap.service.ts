import { Injectable } from '@angular/core';

const getScriptSrc = (callbackName) => {
  return `https://maps.googleapis.com/maps/api/js?key=AIzaSyCj-g_yjS-u4g0Qr-xIjHF7gD9DlfnUboE&callback=${callbackName}`;
}

@Injectable({
  providedIn: 'root'
})

/**
 * Injectable service supporting handling of Google Maps.
 * Based on https://stackoverflow.com/questions/42816363/getting-started-how-to-use-google-maps-api-with-angular-cli
 *
 * Replace getScriptScr with your API key!
 */
export class GMapService {
  private map: google.maps.Map;
  private geocoder: google.maps.Geocoder;
  private scriptLoadingPromise: Promise<void>;

  constructor() {
    this.loadScriptLoadingPromise();
  }

  onReady(): Promise<void> {
    return this.scriptLoadingPromise;
  }

  initMap(mapHtmlElement: HTMLElement, options: google.maps.MapOptions): Promise<google.maps.Map> {
    return this.onReady().then(() => {
      this.map = new google.maps.Map(mapHtmlElement, options);

      // solution from: https://stackoverflow.com/questions/5033650/how-to-dynamically-remove-a-stylesheet-from-the-current-page
      google.maps.event.addListenerOnce(this.map, 'idle', () => {
        const font = document.querySelector('link[href$="//fonts.googleapis.com/css?family=Roboto:300,400,500,700|Google+Sans');
        if (font) {
          font.parentNode.removeChild(font);
        }
      });
      return this.map;
    });
  }

  getMap(): google.maps.Map {
    return this.map;
  }

  addPin(position) {
    return new google.maps.Marker
    ({
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
