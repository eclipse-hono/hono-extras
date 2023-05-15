import {Component, Input, QueryList, ViewChildren} from '@angular/core';
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {DeviceService} from "../../../services/device/device.service";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {DeviceModalComponent} from "../../modals/device/device-modal.component";
import {DeleteComponent} from "../../modals/delete/delete.component";

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

  ngOnInit() {
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

  protected selectGateway(device: Device): void {
    this.router.navigate(['device-detail', device.id], {
      state: {
        tenant: this.tenant,
        device: device,
        isGateway: true,
      },
    });
  }

  protected createDevice(): void {
    const modalRef = this.modalService.open(DeviceModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.result.then((device) => {
      if (device) {
        this.listGateways();
        this.notificationService.success("Successfully created device " + device.id.toBold());
      }
    });
  }

  protected deleteDevice(device: Device): void {
    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Delete';
    modalRef.componentInstance.body = 'Do you really want to delete the device ' + device.id.toBold() + '?';
    modalRef.result.then((res) => {
      if (res) {
        this.delete(device);
      }
    });
  }

  protected gatewayListIsEmpty(): boolean {
    return !this.gateways || this.gateways.length === 0;
  }

  private listGateways() {
    this.deviceService.listByTenant(this.tenant.id, this.pageSize, this.pageOffset).subscribe((listResult) => {
      this.filterGateways(listResult);
    }, (error) => {
      console.log(error);
    });
  }

  private filterGateways(result: any) {
    this.gateways = [];
    const devices: Device[] = result.result;
    for (const device of devices) {
      if (device.via && device.via.length > 0) {
        // devices inside via array are gateways
        device.via.forEach(id => this.setGateways(devices, id));
      }
    }
    this.gatewayListCount = this.gateways.length;
  }

  private setGateways(devices: Device[], gatewayId: string) {
    for (const device of devices) {
      if (device.id === gatewayId && !this.gateways.includes(device)) {
        this.gateways.push(device);
      }
    }
  }

  private delete(gateway: Device) {
    this.deviceService.delete(gateway.id, this.tenant.id).subscribe(() => {
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
