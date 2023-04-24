import {Component, EventEmitter, Output} from '@angular/core';
import {Secret} from "../../../../models/credentials/secret";

@Component({
  selector: 'app-device-password-modal',
  templateUrl: './device-password-modal.component.html',
  styleUrls: ['./device-password-modal.component.scss']
})
export class DevicePasswordModalComponent {

  @Output()
  public passwordSecretChanged: EventEmitter<Secret> = new EventEmitter<Secret>();
  protected passwordSecret: Secret = new Secret();
  protected usePlainPasswordLabel: string = 'Use Plain Password';
  protected usePasswordHashLabel: string = 'Use Password Hash';
  protected plainPasswordLabel: string = 'Plain Password';
  protected hashFunctionLabel: string = 'Password Hash Function';
  protected pwdHashLabel: string = 'Password Hash';
  protected saltLabel: string = 'Salt'
  protected usePlainPassword: boolean = true;

  protected setUsePlainPassword(usePlainPassword: boolean) {
    this.usePlainPassword = usePlainPassword;
    this.passwordSecret = new Secret();
    this.passwordSecretChanged.emit(undefined);
  }

  protected onPasswordSecretChanged() {
    if (this.isInvalid()) {
      this.passwordSecretChanged.emit(undefined);
    } else {
      delete this.passwordSecret.algorithm;
      delete this.passwordSecret.key;
      delete this.passwordSecret.cert;
      if (this.usePlainPassword) {
        delete this.passwordSecret["pwd-hash"];
        delete this.passwordSecret["hash-function"];
        delete this.passwordSecret.salt;
      } else {
        delete this.passwordSecret["pwd-plain"];
      }
      this.passwordSecretChanged.emit(this.passwordSecret);
    }
  }

  private isInvalid(): boolean {
    if (this.usePlainPassword) {
      return !this.passwordSecret["pwd-plain"];
    } else {
      return !this.passwordSecret["pwd-hash"] || !this.passwordSecret["hash-function"];
    }
  }
}
