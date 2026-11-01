import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRabbitComponent } from './admin-rabbit.component';

describe('AdminRabbitComponent', () => {
  let component: AdminRabbitComponent;
  let fixture: ComponentFixture<AdminRabbitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRabbitComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRabbitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
