import {Component, EventEmitter, Input, Output, QueryList, ViewChildren} from '@angular/core';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeviceModalComponent} from '../../modals/device/device-modal.component';
import {DeleteComponent} from '../../modals/delete/delete.component';
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {DeviceService} from "../../../services/device/device.service";
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";
import {NotificationService} from "../../../services/notification/notification.service";

@Component({
  selector: 'app-device-list',
  templateUrl: './device-list.component.html',
  styleUrls: ['./device-list.component.scss']
})
export class DeviceListComponent {

  @ViewChildren(SortableTableDirective)
  public sortableHeaders: QueryList<SortableTableDirective> = new QueryList<SortableTableDirective>();

  @Input()
  public tenant: Tenant = new Tenant();

  @Input()
  public devices: Device[] = [];

  @Input()
  public deviceListCount: number = 0;

  @Input()
  public boundDevicesToGateway: boolean = false;

  @Output()
  public pageSizeChanged: EventEmitter<number> = new EventEmitter<number>();

  @Output()
  public deviceCreated: EventEmitter<any> = new EventEmitter<any>();

  protected deviceListLabel: string = 'Device List';
  protected newDeviceLabel: string = 'Create New Device';
  protected deviceIdLabel: string = 'Device ID';
  protected deviceCreatedLabel: string = 'Created (UTC)'
  protected actionsLabel: string = 'Actions';
  protected deleteLabel: string = 'Delete';
  protected searchLabel: string = 'Search';
  protected displayedItemsDropdownButton: string = 'Displayed Devices';
  protected noDeviceText: string = 'Tenant has no devices yet. Please create a new device.'

  protected searchTerm!: string;

  protected pageSize: number = 50;

  protected pageSizeOptions: number[] = [50, 100, 200];


  constructor(private router: Router,
              private modalService: NgbModal,
              private deviceService: DeviceService,
              private sortableTableService: SortableTableService,
              private notificationService: NotificationService) {
  }

  protected setPageSize(size: number) {
    this.pageSizeChanged.emit(size);
  }

  protected onSort({ column, direction }: SortEvent) {
    this.sortableHeaders = this.sortableTableService.resetHeaders(this.sortableHeaders, column);
    this.devices = this.sortableTableService.sortItems<Device>(this.devices, {column, direction});
  }

  protected getCreationTime(status: any) {
    if (status && status['created']) {
      return status['created'];
    }
    return '-';
  }

  protected selectDevice(device: Device): void {
    this.router.navigate(['device-detail', device.id], {
      state: {
        tenant: this.tenant,
        device: device
      }
    });
  }

  protected createDevice(): void {
    const modalRef = this.modalService.open(DeviceModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.result.then((device) => {
      if (device) {
        this.deviceCreated.emit();
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

  private delete(device: Device) {
    this.deviceService.delete(device.id, this.tenant.id).subscribe(() => {
      const index = this.devices.indexOf(device);
      if (index >= 0) {
        this.devices.splice(index, 1);
        this.deviceListCount = this.deviceListCount -1;
        this.notificationService.success("Successfully deleted device " + device.id.toBold());
      }
    }, (error) => {
      console.log(error);
      this.notificationService.error("Could not delete device " + device.id.toBold());
    })
  }

  protected deviceListIsEmpty(): boolean {
    return !this.devices || this.devices.length === 0;
  }

}
