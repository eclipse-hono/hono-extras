import {Component, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {LoadingSpinnerService} from '../../services/loading-spinner/loading-spinner.service';

@Component({
  selector: 'loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loader-spinner.component.scss']
})
export class LoaderSpinnerComponent implements OnInit {

  protected loaderSubject: Subject<boolean> = this.loaderService.isLoading;

  protected isLoading: boolean = false;

  constructor(private loaderService: LoadingSpinnerService) {
  }

  ngOnInit() {
    this.loaderSubject.subscribe((isLoading) => {
      setTimeout(() => {
        this.isLoading = isLoading;
      });
    })
  }

}
