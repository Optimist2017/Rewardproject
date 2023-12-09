import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TasksService } from '../service/tasks.service';
import { ITasks, Tasks } from '../tasks.model';

import { TasksUpdateComponent } from './tasks-update.component';

describe('Tasks Management Update Component', () => {
  let comp: TasksUpdateComponent;
  let fixture: ComponentFixture<TasksUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tasksService: TasksService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TasksUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TasksUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TasksUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tasksService = TestBed.inject(TasksService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tasks: ITasks = { id: 456 };

      activatedRoute.data = of({ tasks });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tasks));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tasks>>();
      const tasks = { id: 123 };
      jest.spyOn(tasksService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tasks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tasks }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tasksService.update).toHaveBeenCalledWith(tasks);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tasks>>();
      const tasks = new Tasks();
      jest.spyOn(tasksService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tasks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tasks }));
      saveSubject.complete();

      // THEN
      expect(tasksService.create).toHaveBeenCalledWith(tasks);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tasks>>();
      const tasks = { id: 123 };
      jest.spyOn(tasksService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tasks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tasksService.update).toHaveBeenCalledWith(tasks);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
