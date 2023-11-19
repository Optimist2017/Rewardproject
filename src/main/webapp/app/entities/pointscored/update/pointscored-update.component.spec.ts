import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PointscoredService } from '../service/pointscored.service';
import { IPointscored, Pointscored } from '../pointscored.model';
import { ITasks } from 'app/entities/tasks/tasks.model';
import { TasksService } from 'app/entities/tasks/service/tasks.service';

import { PointscoredUpdateComponent } from './pointscored-update.component';

describe('Pointscored Management Update Component', () => {
  let comp: PointscoredUpdateComponent;
  let fixture: ComponentFixture<PointscoredUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pointscoredService: PointscoredService;
  let tasksService: TasksService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PointscoredUpdateComponent],
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
      .overrideTemplate(PointscoredUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PointscoredUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pointscoredService = TestBed.inject(PointscoredService);
    tasksService = TestBed.inject(TasksService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tasks query and add missing value', () => {
      const pointscored: IPointscored = { id: 456 };
      const name: ITasks = { id: 99880 };
      pointscored.name = name;

      const tasksCollection: ITasks[] = [{ id: 92595 }];
      jest.spyOn(tasksService, 'query').mockReturnValue(of(new HttpResponse({ body: tasksCollection })));
      const additionalTasks = [name];
      const expectedCollection: ITasks[] = [...additionalTasks, ...tasksCollection];
      jest.spyOn(tasksService, 'addTasksToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pointscored });
      comp.ngOnInit();

      expect(tasksService.query).toHaveBeenCalled();
      expect(tasksService.addTasksToCollectionIfMissing).toHaveBeenCalledWith(tasksCollection, ...additionalTasks);
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pointscored: IPointscored = { id: 456 };
      const name: ITasks = { id: 63451 };
      pointscored.name = name;

      activatedRoute.data = of({ pointscored });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pointscored));
      expect(comp.tasksSharedCollection).toContain(name);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pointscored>>();
      const pointscored = { id: 123 };
      jest.spyOn(pointscoredService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pointscored });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pointscored }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(pointscoredService.update).toHaveBeenCalledWith(pointscored);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pointscored>>();
      const pointscored = new Pointscored();
      jest.spyOn(pointscoredService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pointscored });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pointscored }));
      saveSubject.complete();

      // THEN
      expect(pointscoredService.create).toHaveBeenCalledWith(pointscored);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pointscored>>();
      const pointscored = { id: 123 };
      jest.spyOn(pointscoredService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pointscored });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pointscoredService.update).toHaveBeenCalledWith(pointscored);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTasksById', () => {
      it('Should return tracked Tasks primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTasksById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
