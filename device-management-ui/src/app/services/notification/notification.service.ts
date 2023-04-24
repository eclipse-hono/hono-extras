import {Injectable, TemplateRef} from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class NotificationService {

  public toasts: any[] = [];

  private show(textOrTpl: string | TemplateRef<any>, header: string, options: any = {}) {
    this.toasts.push({textOrTpl, header, ...options});
  }

  public success(message: string) {
    this.show(message, 'Success', {classname: 'bg-success text-light', autohide: true});
  }

  public warning(message: string) {
    this.show(message, 'Warning', {classname: 'bg-warning text-light', autohide: true});
  }

  public error(message: string) {
    this.show(message, 'Error', {classname: 'bg-danger text-light', autohide: false});
  }

  public remove(toast: any) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }
}
