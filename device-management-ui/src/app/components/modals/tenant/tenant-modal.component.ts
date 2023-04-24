import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Tenant} from "../../../models/tenant";
import {TenantService} from "../../../services/tenant/tenant.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-tenant-modal',
  templateUrl: './tenant-modal.component.html',
  styleUrls: ['./tenant-modal.component.scss'],

})
export class TenantModalComponent implements OnInit {

  @Input()
  public isNewTenant: boolean = true;

  @Input()
  public tenant: Tenant = new Tenant();

  protected tenantIdLabel = 'Tenant ID';
  protected tenantMessagingTypeLabel = 'Messaging Type';
  protected confirmButtonLabel = 'Save';

  protected modalTitle: string = '';

  protected messagingTypes: {
    key: string,
    value: string,
  }[] = [
    {key: 'pubsub', value: 'Pub/Sub'},
    {key: 'kafka', value: 'Kafka'},
    {key: 'amqp', value: 'AMQP'}
  ]

  constructor(private activeModal: NgbActiveModal,
              private tenantService: TenantService,
              private notificationService: NotificationService) {

  }

  ngOnInit() {
    if (this.isNewTenant) {
      this.modalTitle = 'Create Tenant';
    } else {
      this.modalTitle = 'Edit Tenant';
    }
    if (!this.tenant.ext) {
      this.tenant.ext = {'messaging-type': ''};
    }
  }

  protected onConfirm() {
    if (this.isInvalid()) {
      return;
    }
    if (this.isNewTenant) {
      this.createTenant();
    } else {
      this.updateTenant();
    }
  }

  protected onClose() {
    this.activeModal.close();
  }

  protected isInvalid() {
    return !this.tenant || !this.tenant.id || !this.tenant.ext || !this.tenant.ext['messaging-type'];
  }

  private createTenant() {
    this.tenantService.create(this.tenant).subscribe((result) => {
      if (result) {
        this.activeModal.close(this.tenant);
      }
    }, (error) => {
      console.log('Error saving tenant ', this.tenant.id, error);
      this.notificationService.error('Could not create tenant');
    });
  }

  private updateTenant() {
    this.tenantService.update(this.tenant).subscribe(() => {
      this.activeModal.close(this.tenant);
    }, (error) => {
      console.log('Error saving tenant ' + this.tenant.id, error);
      this.notificationService.error('Could not update tenant');
    });
  }
}
