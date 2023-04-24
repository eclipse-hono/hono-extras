import {Component} from '@angular/core';
import {Subject} from 'rxjs';
import {LoadingSpinnerService} from '../../services/loading-spinner/loading-spinner.service';

@Component({
  selector: 'loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loader-spinner.component.scss']
})
export class LoaderSpinnerComponent {
  protected isLoading: Subject<boolean> = this.loaderService.isLoading;

  constructor(private loaderService: LoadingSpinnerService) {
  }

}
