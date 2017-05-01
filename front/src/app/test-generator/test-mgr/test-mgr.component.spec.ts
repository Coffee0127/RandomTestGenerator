import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestMgrComponent } from './test-mgr.component';

describe('TestMgrComponent', () => {
  let component: TestMgrComponent;
  let fixture: ComponentFixture<TestMgrComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestMgrComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestMgrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
