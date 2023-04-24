import {Component, OnInit, QueryList, ViewChildren} from '@angular/core';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TenantModalComponent} from '../../modals/tenant/tenant-modal.component';
import {DeleteComponent} from '../../modals/delete/delete.component';
import {Tenant} from "../../../models/tenant";
import {TenantService} from "../../../services/tenant/tenant.service";
import {NotificationService} from 'src/app/services/notification/notification.service';
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";

@Component({
  selector: 'app-tenant-list',
  templateUrl: './tenant-list.component.html',
  styleUrls: ['./tenant-list.component.scss']
})
export class TenantListComponent implements OnInit {

  @ViewChildren(SortableTableDirective)
  public sortableHeaders: QueryList<SortableTableDirective> = new QueryList<SortableTableDirective>();

  public tenants: Tenant[] = [];
  protected tenantListLabel: string = 'Tenants';
  protected newTenantLabel: string = 'Create New Tenant';
  protected idLabel: string = 'Tenant ID';
  protected messagingTypeLabel: string = 'Messaging-Type';
  protected actionsLabel: string = 'Actions';
  protected editLabel: string = 'Edit';
  protected deleteLabel: string = 'Delete';
  protected searchLabel: string = 'Search';
  protected displayedItemsDropdownButton: string = 'Displayed Tenants';
  protected noTenantText: string = 'There are no tenants yet. Please create a new tenant.'
  protected tenantListCount: number = 0;
  protected searchTerm!: string;
  protected pageSize: number = 50;
  protected pageSizeOptions: number[] = [50, 100, 200];
  private pageOffset: number = 0;

  constructor(private router: Router,
              public modalService: NgbModal,
              private notificationService: NotificationService,
              private tenantService: TenantService,
              private sortableTableService: SortableTableService) {

  }

  ngOnInit() {
    this.listTenants();
  }

  protected changePage($event: number) {
    this.pageOffset = ($event - 1) * this.pageSize;
    this.listTenants();
  }

  protected setPageSize(size: number) {
    this.pageSize = size;
    this.pageOffset = 0;
    this.listTenants();
  }

  protected onSort({ column, direction }: SortEvent) {
    this.sortableHeaders = this.sortableTableService.resetHeaders(this.sortableHeaders, column);
    this.tenants = this.sortableTableService.sortItems<Tenant>(this.tenants, {column, direction});
  }

  protected getMessagingType(ext: any) {
    if (ext && ext['messaging-type']) {
      return ext['messaging-type'];
    }
    return '-';
  }

  protected selectTenant(tenant: Tenant) {
    this.router.navigate(['tenant-detail', tenant.id], {
      state: {
        tenant: tenant
      }
    });
  }

  protected createTenant(): void {
    const modalRef = this.modalService.open(TenantModalComponent, {size: 'lg'});
    modalRef.componentInstance.modalTitle = 'Create new tenant';
    modalRef.result.then((tenant) => {
      if (tenant) {
        this.tenants = [tenant, ...this.tenants];
        this.tenantListCount = this.tenantListCount + 1;
        this.notificationService.success('Successfully created tenant ' + tenant.id.toBold());
      }
    });
  }

  protected editTenant(tenant: Tenant): void {
    const modalRef = this.modalService.open(TenantModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenant = tenant;
    modalRef.componentInstance.isNewTenant = false;
    modalRef.result.then((res) => {
      if (res) {
        this.notificationService.success('Successfully edited tenant ' + tenant.id.toBold())
      }
    });
  }

  protected deleteTenant(tenant: Tenant): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Delete tenant ' + tenant.id.toBold();
    modalRef.componentInstance.body = 'Do you really want to delete the tenant ' + tenant.id.toBold() + '?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete(tenant);
      }
    });
  }

  protected tenantListIsEmpty(): boolean {
    return !this.tenants || this.tenants.length === 0;
  }

  private listTenants() {
    this.tenantService.list(this.pageSize, this.pageOffset).subscribe(tenants => {
      this.tenants = tenants.result;
      this.tenantListCount = tenants.total;
    }, (err) => {
      console.log(err);
      this.notificationService.error('Could not retrieve tenant list.');
    });
  }

  private delete(tenant: Tenant) {
    this.tenantService.delete(tenant.id).subscribe(() => {
      const index = this.tenants.indexOf(tenant);
      if (index >= 0) {
        this.tenants.splice(index, 1);
        this.tenantListCount = this.tenantListCount - 1;
        this.notificationService.success('Successfully deleted tenant ' + tenant.id.toBold());
      }
    }, (error) => {
      console.log(error);
      this.notificationService.error('Could not delete tenant ' + tenant.id.toBold());
    })
  }
}
