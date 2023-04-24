import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-modal-head',
  templateUrl: './modal-head.component.html',
  styleUrls: ['./modal-head.component.scss']
})
export class ModalHeadComponent {

  @Input()
  public modalTitle: string = '';

  @Output()
  public closeModal: EventEmitter<boolean> = new EventEmitter<boolean>();

  protected cancel() {
    this.closeModal.emit(true);
  }

}
