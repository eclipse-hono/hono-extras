import {Component, TemplateRef} from '@angular/core';
import {NotificationService} from "../../services/notification/notification.service";

@Component({
  selector: 'toast-container',
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss'],
  host: {class: 'toast-container bottom-0 end-0 p-3'},
})
export class ToastContainerComponent {
  constructor(public notificationService: NotificationService) {
  }

  protected isTemplate(toast: any) {
    return toast.textOrTpl instanceof TemplateRef;
  }
}
