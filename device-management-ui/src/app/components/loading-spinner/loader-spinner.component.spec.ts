import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoaderSpinnerComponent } from './loader-spinner.component';

describe('LoaderComponent', () => {
  let component: LoaderSpinnerComponent;
  let fixture: ComponentFixture<LoaderSpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoaderSpinnerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoaderSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
