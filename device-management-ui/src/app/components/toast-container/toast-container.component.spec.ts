import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ToastContainerComponent} from './toast-container.component';
import {NotificationService} from "../../services/notification/notification.service";

describe('ToastContainerComponent', () => {
  let component: ToastContainerComponent;
  let fixture: ComponentFixture<ToastContainerComponent>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ToastContainerComponent],
      providers: [NotificationService]
    })
      .compileComponents();
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['notify']);
    component = new ToastContainerComponent(notificationServiceSpy);

    fixture = TestBed.createComponent(ToastContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return false when toast is not of instance TemplateRef', () => {
    const toast = {textOrTpl: 'some text'};
    expect(component['isTemplate'](toast)).toBeFalse();
  });

});
