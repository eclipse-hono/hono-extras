import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-modal-footer',
  templateUrl: './modal-footer.component.html',
  styleUrls: ['./modal-footer.component.scss']
})
export class ModalFooterComponent {

  @Input()
  public confirmButtonLabel: string = '';

  @Input()
  public buttonDisabled: boolean = false;

  @Input()
  public showRequiredFieldInfo: boolean = true;

  @Output()
  public confirmButtonPressed: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  public cancelButtonPressed: EventEmitter<boolean> = new EventEmitter<boolean>();

  protected cancelButtonLabel: string = 'Cancel';

  protected confirm() {
    this.confirmButtonPressed.emit(true);
  }

  protected cancel() {
    this.cancelButtonPressed.emit(true);
  }
}
