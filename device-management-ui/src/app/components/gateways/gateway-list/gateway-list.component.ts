import {Component, Input, QueryList, ViewChildren} from '@angular/core';
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {DeviceService} from "../../../services/device/device.service";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {DeleteComponent} from "../../modals/delete/delete.component";
import {CreateAndBindModalComponent} from "../../modals/create-and-bind-modal/create-and-bind-modal.component";

@Component({
  selector: 'app-gateway-list',
  templateUrl: './gateway-list.component.html',
  styleUrls: ['./gateway-list.component.scss']
})
export class GatewayListComponent {

  @ViewChildren(SortableTableDirective)
  public sortableHeaders: QueryList<SortableTableDirective> = new QueryList<SortableTableDirective>();

  @Input()
  public tenant: Tenant = new Tenant();

  protected gatewayListLabel: string = 'Gateway List';
  protected newGatewayLabel: string = 'Create New Gateway';
  protected gatewayIdLabel: string = 'Gateway ID';
  protected gatewayCreatedLabel: string = 'Created (UTC)'
  protected actionsLabel: string = 'Actions';
  protected deleteLabel: string = 'Delete';
  protected searchLabel: string = 'Search';
  protected displayedItemsDropdownButton: string = 'Displayed Gateways';
  protected noGatewayText: string = 'Tenant has no gateways yet to display.'
  protected gatewayListCount: number = 0;
  protected searchTerm!: string;
  protected pageSize: number = 50;
  protected pageSizeOptions: number[] = [50, 100, 200];
  protected gateways: Device[] = [];
  private pageOffset: number = 0;

  constructor(private router: Router,
              private modalService: NgbModal,
              private deviceService: DeviceService,
              private sortableTableService: SortableTableService,
              private notificationService: NotificationService) {
  }

  public ngOnInit() {
    this.listGateways();
  }

  protected changePage($event: number) {
    this.pageOffset = ($event - 1) * this.pageSize;
    this.listGateways();
  }

  protected setPageSize(size: number) {
    this.pageSize = size;
    this.pageOffset = 0;
    this.listGateways();
  }

  protected onSort({column, direction}: SortEvent) {
    this.sortableHeaders = this.sortableTableService.resetHeaders(this.sortableHeaders, column);
    this.gateways = this.sortableTableService.sortItems<Device>(this.gateways, {column, direction});
  }

  protected getCreationTime(status: any) {
    if (status && status['created']) {
      return status['created'];
    }
    return '-';
  }

  protected selectGateway(gateway: Device): void {
    this.router.navigate(['device-detail', gateway.id], {
      state: {
        tenant: this.tenant,
        device: gateway,
        isGateway: true,
      },
    });
  }

  protected createGateway(): void {
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.isGatewayFlag = true;
    modalRef.result.then((gateway) => {
      if (gateway) {
        this.gateways = [...this.gateways,gateway]
        this.notificationService.success("Successfully created gateway " + gateway.id.toBold());
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected deleteGateway(gateway: Device): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the gateway ' + gateway.id.toBold() + '?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete(gateway);
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected gatewayListIsEmpty(): boolean {
    return !this.gateways || this.gateways.length === 0;
  }

  private listGateways() {
    this.deviceService.listByTenant(this.tenant.id, this.pageSize, this.pageOffset, true).subscribe((listResult) => {
      this.gateways = listResult.result;
      this.gatewayListCount = listResult.total;
    }, (error) => {
      console.log(error);
    });
  }

  private delete(gateway: Device) {
    this.deviceService.delete(gateway, this.tenant.id).subscribe(() => {

      this.deviceService.listBoundDevices(this.tenant.id, gateway.id, this.pageSize, this.pageOffset).subscribe((deviceList) => {
        const boundDevices = deviceList.result;
        boundDevices.forEach((boundDevice: Device) => {
          if (boundDevice.via != null) {
            const index = boundDevice.via.indexOf(gateway.id)
            if (index >= 0) {
              boundDevice.via.splice(index, 1);

              this.deviceService.update(boundDevice, this.tenant.id).subscribe((result) => {
                console.log('update result: ', result);
              });
            }
          }
        });
      });

      const index = this.gateways.indexOf(gateway);
      if (index >= 0) {
        this.gateways.splice(index, 1);
        this.gatewayListCount = this.gatewayListCount - 1;
        this.notificationService.success("Successfully deleted gateway " + gateway.id.toBold());
      }
    }, (error) => {
      console.log(error);
      this.notificationService.error("Could not delete gateway " + gateway.id.toBold());
    })
  }
}
