import { Component } from '@angular/core';
import './prototypes/string-prototype1'
import {GoogleService} from "./services/google/google.service";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'device-management-ui';

  constructor(private googleService: GoogleService) {
  }

}
