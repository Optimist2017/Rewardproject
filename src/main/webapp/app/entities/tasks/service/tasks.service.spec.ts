import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITasks, Tasks } from '../tasks.model';

import { TasksService } from './tasks.service';

describe('Tasks Service', () => {
  let service: TasksService;
  let httpMock: HttpTestingController;
  let elemDefault: ITasks;
  let expectedResult: ITasks | ITasks[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TasksService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      url: 'AAAAAAA',
      description: 'AAAAAAA',
      point: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Tasks', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Tasks()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Tasks', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          url: 'BBBBBB',
          description: 'BBBBBB',
          point: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Tasks', () => {
      const patchObject = Object.assign(
        {
          url: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Tasks()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Tasks', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          url: 'BBBBBB',
          description: 'BBBBBB',
          point: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Tasks', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTasksToCollectionIfMissing', () => {
      it('should add a Tasks to an empty array', () => {
        const tasks: ITasks = { id: 123 };
        expectedResult = service.addTasksToCollectionIfMissing([], tasks);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tasks);
      });

      it('should not add a Tasks to an array that contains it', () => {
        const tasks: ITasks = { id: 123 };
        const tasksCollection: ITasks[] = [
          {
            ...tasks,
          },
          { id: 456 },
        ];
        expectedResult = service.addTasksToCollectionIfMissing(tasksCollection, tasks);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tasks to an array that doesn't contain it", () => {
        const tasks: ITasks = { id: 123 };
        const tasksCollection: ITasks[] = [{ id: 456 }];
        expectedResult = service.addTasksToCollectionIfMissing(tasksCollection, tasks);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tasks);
      });

      it('should add only unique Tasks to an array', () => {
        const tasksArray: ITasks[] = [{ id: 123 }, { id: 456 }, { id: 1969 }];
        const tasksCollection: ITasks[] = [{ id: 123 }];
        expectedResult = service.addTasksToCollectionIfMissing(tasksCollection, ...tasksArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tasks: ITasks = { id: 123 };
        const tasks2: ITasks = { id: 456 };
        expectedResult = service.addTasksToCollectionIfMissing([], tasks, tasks2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tasks);
        expect(expectedResult).toContain(tasks2);
      });

      it('should accept null and undefined values', () => {
        const tasks: ITasks = { id: 123 };
        expectedResult = service.addTasksToCollectionIfMissing([], null, tasks, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tasks);
      });

      it('should return initial array if no Tasks is added', () => {
        const tasksCollection: ITasks[] = [{ id: 123 }];
        expectedResult = service.addTasksToCollectionIfMissing(tasksCollection, undefined, null);
        expect(expectedResult).toEqual(tasksCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
