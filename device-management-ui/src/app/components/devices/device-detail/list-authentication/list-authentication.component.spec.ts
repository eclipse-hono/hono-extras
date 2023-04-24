import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ListAuthenticationComponent} from './list-authentication.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {OAuthModule} from "angular-oauth2-oidc";
import {CredentialTypes} from "../../../../models/credentials/credentials";

describe('ListAuthenticationComponent', () => {
  let component: ListAuthenticationComponent;
  let fixture: ComponentFixture<ListAuthenticationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, OAuthModule.forRoot()],
      declarations: [ ListAuthenticationComponent ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListAuthenticationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return true for RPK credentials', () => {
    const authenticationValue = {type: CredentialTypes.RPK};
    expect(component['isEditable'](authenticationValue)).toBe(true);
  });

  it('should return false for HASHED_PASSWORD credentials', () => {
    const authenticationValue = {type: CredentialTypes.HASHED_PASSWORD};
    expect(component['isEditable'](authenticationValue)).toBe(false);
  });

  it('should return "-" when type is undefined', () => {
    const result = component['getAuthenticationType'](undefined);

    expect(result).toEqual('-');
  });

  it('should return "JWT based" when type is RPK', () => {
    const result = component['getAuthenticationType'](CredentialTypes.RPK);

    expect(result).toEqual('JWT based');
  });

  it('should return "Password based" when type is HASHED_PASSWORD', () => {
    const result = component['getAuthenticationType'](CredentialTypes.HASHED_PASSWORD);

    expect(result).toEqual('Password based');
  });

});
