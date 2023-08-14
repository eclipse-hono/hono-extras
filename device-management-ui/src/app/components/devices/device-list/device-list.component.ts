import {Component, EventEmitter, Input, Output, QueryList, ViewChildren} from '@angular/core';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeleteComponent} from '../../modals/delete/delete.component';
import {Tenant} from "../../../models/tenant";
import {Device} from "../../../models/device";
import {DeviceService} from "../../../services/device/device.service";
import {SortableTableDirective, SortEvent} from "../../../services/sortable-table/sortable-table.directive";
import {SortableTableService} from "../../../services/sortable-table/sortable-table.service";
import {NotificationService} from "../../../services/notification/notification.service";
import {CreateAndBindModalComponent} from "../../modals/create-and-bind-modal/create-and-bind-modal.component";

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
  public deviceId: string = '';

  @Input()
  public isGateway!: boolean;

  @Input()
  public deviceListCount: number = 0;

  @Input()
  public boundDeviceListCount: number = 0;

  @Input()
  public boundDevicesToGateway: boolean = false;


  @Input()
  public unbindDevices: boolean = false;

  @Output()
  public pageSizeChanged: EventEmitter<number> = new EventEmitter<number>();

  @Output()
  public deviceCreated: EventEmitter<any> = new EventEmitter<any>();

  protected deviceListLabel: string = 'Device List';
  protected newDeviceLabel: string = 'Create New Device';
  protected bindDeviceLabel: string = 'Bind Device(s)';
  protected unBindDeviceLabel: string = 'Unbind Device(s)';
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
  protected selectedDevices: Device[] = [];

  @Output()
  public selectedDevicesChanged: EventEmitter<Device[]> = new EventEmitter<Device[]>();


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
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.isDeviceFlag = true;
    modalRef.result.then((device) => {
      if (device) {
        this.deviceCreated.emit();
        this.notificationService.success("Successfully created device " + device.id.toBold());
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
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
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  private delete(device: Device) {
    this.deviceService.delete(device, this.tenant.id).subscribe(() => {
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

  protected unbindDevicesFromGateway(){
    this.unbindDevices = true;

    const modalRef = this.modalService.open(DeleteComponent, {ariaLabelledBy: 'modal-basic-title'});
    modalRef.componentInstance.modalTitle = 'Confirm Unbind';
    modalRef.componentInstance.body = 'Do you really want to unbind the selected devices?';
    modalRef.componentInstance.unbind = true;

    modalRef.result.then((res) => {
      if(res) {
        this.unbind();
      }
    }, (reason: any) => {
      console.log(`Closed with reason: ${reason}`);
    });
  }

  protected bindNewDevicesToGateway(){
    const modalRef = this.modalService.open(CreateAndBindModalComponent, {size: 'lg'});
    modalRef.componentInstance.tenantId = this.tenant.id;
    modalRef.componentInstance.deviceId = this.deviceId;
    modalRef.componentInstance.isBindDeviceFlag = true;
    modalRef.componentInstance.boundDevicesCount = this.boundDeviceListCount;
    modalRef.componentInstance.isGateway = this.isGateway;

    modalRef.componentInstance.devicesSelected.subscribe((selectedDevices: Device[]) => {
      this.devices.push(...selectedDevices);
    });
  }

  private unbind() {
    for (const selectedDevice of this.selectedDevices) {
      const index= selectedDevice.via?.indexOf(this.deviceId);
      if (index !== -1) {
        selectedDevice.via?.splice(index as number, 1);
        for (let i = 0; i < this.devices.length; i++) {
          if (selectedDevice === this.devices[i]) {
            this.devices.splice(i, 1);
            break;
          }
        }
        this.deviceService.update(selectedDevice, this.tenant.id).subscribe(
          () => {
            this.boundDeviceListCount = this.devices.length
            if (this.devices.length <= 0) {
              this.navigateBack();
            }
          },
          (error) => {
            console.log('Error updating device after unbinding: ', selectedDevice, error);
            this.notificationService.error('Could not update device after unbinding');
          }
        );
      }
    }
  }

  protected navigateBack() {
    this.router.navigate(['tenant-detail', this.tenant.id], {
      state: {
        tenant: this.tenant
      }
    });
  }

  public devicesSelectedCheck(): boolean{
    return this.devices.some(device => device.checked);
  }

  protected markDevice(selectedDevice: Device) {
    selectedDevice.checked = !selectedDevice.checked;
    this.selectedDevices = this.devices.filter(device => device.checked);
    this.selectedDevicesChanged.emit(this.selectedDevices);
  }
}
