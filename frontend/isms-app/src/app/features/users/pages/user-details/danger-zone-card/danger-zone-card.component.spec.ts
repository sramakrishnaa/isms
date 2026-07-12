import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DangerZoneCardComponent } from './danger-zone-card.component';

describe('DangerZoneCardComponent', () => {
  let component: DangerZoneCardComponent;
  let fixture: ComponentFixture<DangerZoneCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DangerZoneCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DangerZoneCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
