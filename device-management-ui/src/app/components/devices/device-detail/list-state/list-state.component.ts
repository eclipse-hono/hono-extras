import {Component, Input, OnInit} from '@angular/core';
import {StatesService} from "../../../../services/states/states.service";
import {State} from "../../../../models/state";

@Component({
  selector: 'app-list-state',
  templateUrl: './list-state.component.html',
  styleUrls: ['./list-state.component.scss']
})
export class ListStateComponent implements OnInit {

  @Input()
  public deviceId: string = '';

  @Input()
  public tenantId: string = '';
  protected updateLabel: string = 'Cloud update time (UTC)';
  protected dataLabel: string = 'Data';

  protected states: State[] = [];

  constructor(private statesService: StatesService) {
  }

  ngOnInit() {
    this.getStates();
  }

  private getStates() {
    this.statesService.list(this.deviceId, this.tenantId).subscribe((states) => {
      this.states = states.deviceStates;
    }, (error) => {
      console.log(error);
    })
  }
}
