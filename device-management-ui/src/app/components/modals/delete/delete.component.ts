import {Component, Input} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  styleUrls: ['./delete.component.scss']
})
export class DeleteComponent {

  @Input()
  public modalTitle: string = '';

  @Input()
  public body: string = '';

  protected deleteButtonLabel = 'Delete';

  constructor(private activeModal: NgbActiveModal) {
  }

  protected onConfirm() {
    this.activeModal.close(true);
  }

  protected onCancel() {
    this.activeModal.close();
  }

}
