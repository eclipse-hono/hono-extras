import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GatewayListComponent} from './gateway-list.component';

describe('GatewayListComponent', () => {
  let component: GatewayListComponent;
  let fixture: ComponentFixture<GatewayListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GatewayListComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GatewayListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
