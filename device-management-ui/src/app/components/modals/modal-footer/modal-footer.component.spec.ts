import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ModalFooterComponent} from './modal-footer.component';
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";

describe('ModalFooterComponent', () => {
  let component: ModalFooterComponent;
  let fixture: ComponentFixture<ModalFooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FontAwesomeTestingModule],
      declarations: [ModalFooterComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit confirmButtonPressed event when confirm() is called', () => {
    spyOn(component.confirmButtonPressed, 'emit');
    component['confirm']();
    expect(component.confirmButtonPressed.emit).toHaveBeenCalledWith(true);
  });

  it('should emit cancelButtonPressed event when cancel() is called', () => {
    spyOn(component.cancelButtonPressed, 'emit');
    component['cancel']();
    expect(component.cancelButtonPressed.emit).toHaveBeenCalledWith(true);
  });

});
