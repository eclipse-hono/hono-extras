import {Injectable} from '@angular/core';
import {HttpHeaders} from "@angular/common/http";
import {GoogleService} from "../google/google.service";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private googleService: GoogleService) {
  }

  public getHttpsRequestOptions() {
    const idToken = this.googleService.getIdToken();
    const header = new HttpHeaders()
      .set('Accept', 'application/json')
      .set('Authorization', `Bearer ${idToken}`)

    return {
      headers: header,
      withCredentials: true,
      observe: 'body' as 'response'
    }
  }

}
