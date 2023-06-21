import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GatewayModalComponent } from './gateway-modal.component';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpClientModule} from "@angular/common/http";
import {OAuthModule} from "angular-oauth2-oidc";
import {ModalHeadComponent} from "../modal-head/modal-head.component";
import {NgSelectModule} from "@ng-select/ng-select";
import {ModalFooterComponent} from "../modal-footer/modal-footer.component";
import {FormsModule} from "@angular/forms";
import {FontAwesomeTestingModule} from "@fortawesome/angular-fontawesome/testing";

describe('GatewayModalComponent', () => {
  let component: GatewayModalComponent;
  let fixture: ComponentFixture<GatewayModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GatewayModalComponent, ModalHeadComponent, ModalFooterComponent],
      providers: [NgbActiveModal],
      imports: [HttpClientModule, OAuthModule.forRoot(), NgSelectModule, FontAwesomeTestingModule, FormsModule]
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
