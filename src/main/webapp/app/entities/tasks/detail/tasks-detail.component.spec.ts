import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TasksDetailComponent } from './tasks-detail.component';

describe('Tasks Management Detail Component', () => {
  let comp: TasksDetailComponent;
  let fixture: ComponentFixture<TasksDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TasksDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tasks: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TasksDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TasksDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tasks on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tasks).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
