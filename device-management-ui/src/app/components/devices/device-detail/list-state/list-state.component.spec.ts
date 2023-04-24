import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ListStateComponent} from './list-state.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";

describe('ListStateComponent', () => {
  let component: ListStateComponent;
  let fixture: ComponentFixture<ListStateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot()],
      declarations: [ListStateComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListStateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
