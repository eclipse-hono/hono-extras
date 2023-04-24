import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Secret} from "../../../../models/credentials/secret";

@Component({
  selector: 'app-device-rpk-modal',
  templateUrl: './device-rpk-modal.component.html',
  styleUrls: ['./device-rpk-modal.component.scss']
})
export class DeviceRpkModalComponent implements OnInit {

  @Output()
  public rpkSecretChanged: EventEmitter<Secret> = new EventEmitter<Secret>();

  @Input()
  public rpkSecret: Secret = new Secret();

  @Input()
  public isNewCredentials: boolean = true;

  protected algorithmLabel: string = 'Algorithm';
  protected publicKeyValueLabel: string = 'Public Key value';
  protected certValueLabel: string = 'X509 Certificate value';
  protected notBeforeLabel: string = 'Not before';
  protected notAfterLabel: string = 'Not after';
  protected publicKeyLabel: string = 'Public Key';
  protected certificateLabel: string = 'X509 Certificate';
  protected certTooltip: string = 'Please be aware that the X509 Certificate will be converted into a Public Key after it is saved. So you won´t longer see information about the certificate, but the public key instead.'

  protected usePublicKey: boolean = true;

  protected notBefore: boolean = false;

  protected notAfter: boolean = false;

  protected algorithmTypes: string[] = ['EC', 'RSA'];

  ngOnInit() {
    if (!this.rpkSecret) {
      this.rpkSecret = new Secret();
    }
    if (this.rpkSecret["not-before"]) {
      this.notBefore = true;
    }
    if (this.rpkSecret["not-after"]) {
      this.notAfter = true;
    }
  }

  protected setUsePublicKey(usePublicKey: boolean) {
    this.usePublicKey = usePublicKey;
    this.rpkSecret = new Secret();
    this.rpkSecretChanged.emit(undefined);
  }

  protected onRpkSecretChanged() {
    if (this.isInvalid()) {
      this.rpkSecretChanged.emit(undefined);
    } else {
      if (this.usePublicKey) {
        delete this.rpkSecret.cert;
      } else {
        delete this.rpkSecret.algorithm;
        delete this.rpkSecret.key;
      }
      this.rpkSecretChanged.emit(this.rpkSecret);
    }
  }

  protected setNotBeforeDateTime($event: any) {
    this.rpkSecret["not-before"] = this.setDateTime($event);
    this.onRpkSecretChanged();
  }

  protected setNotAfterDateTime($event: any) {
    this.rpkSecret["not-after"] = this.setDateTime($event);
    this.onRpkSecretChanged();
  }

  private setDateTime($event: any): string | undefined {
    if (!$event || !$event.date || !$event.time || $event.time.hour === undefined || $event.time.minute === undefined || $event.time.second === undefined) {
      return undefined;
    }

    const date = new Date($event.date.year, $event.date.month, $event.date.day);
    date.setHours(Number($event.time.hour));
    date.setMinutes(Number($event.time.minute));
    date.setSeconds($event.time.second);
    return date.toISOString();
  }

  private isInvalid(): boolean {
    if (this.usePublicKey) {
      return !this.rpkSecret.key || !this.rpkSecret.algorithm;
    } else {
      return !this.rpkSecret.cert;
    }
  }

}
