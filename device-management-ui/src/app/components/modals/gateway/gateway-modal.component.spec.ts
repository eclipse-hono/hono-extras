import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GatewayModalComponent } from './gateway-modal.component';

describe('GatewayModalComponent', () => {
  let component: GatewayModalComponent;
  let fixture: ComponentFixture<GatewayModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GatewayModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GatewayModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
