import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ModalHeadComponent} from './modal-head.component';

describe('ModalHeadComponent', () => {
  let component: ModalHeadComponent;
  let fixture: ComponentFixture<ModalHeadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModalHeadComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalHeadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit the closeModal event when cancel() is called', () => {
    spyOn(component.closeModal, 'emit');
    component['cancel']();
    expect(component.closeModal.emit).toHaveBeenCalledWith(true);
  });

});
