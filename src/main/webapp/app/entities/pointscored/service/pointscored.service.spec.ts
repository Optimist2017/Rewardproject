import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPointscored, Pointscored } from '../pointscored.model';

import { PointscoredService } from './pointscored.service';

describe('Pointscored Service', () => {
  let service: PointscoredService;
  let httpMock: HttpTestingController;
  let elemDefault: IPointscored;
  let expectedResult: IPointscored | IPointscored[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PointscoredService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Pointscored', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Pointscored()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pointscored', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pointscored', () => {
      const patchObject = Object.assign({}, new Pointscored());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pointscored', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Pointscored', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPointscoredToCollectionIfMissing', () => {
      it('should add a Pointscored to an empty array', () => {
        const pointscored: IPointscored = { id: 123 };
        expectedResult = service.addPointscoredToCollectionIfMissing([], pointscored);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pointscored);
      });

      it('should not add a Pointscored to an array that contains it', () => {
        const pointscored: IPointscored = { id: 123 };
        const pointscoredCollection: IPointscored[] = [
          {
            ...pointscored,
          },
          { id: 456 },
        ];
        expectedResult = service.addPointscoredToCollectionIfMissing(pointscoredCollection, pointscored);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pointscored to an array that doesn't contain it", () => {
        const pointscored: IPointscored = { id: 123 };
        const pointscoredCollection: IPointscored[] = [{ id: 456 }];
        expectedResult = service.addPointscoredToCollectionIfMissing(pointscoredCollection, pointscored);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pointscored);
      });

      it('should add only unique Pointscored to an array', () => {
        const pointscoredArray: IPointscored[] = [{ id: 123 }, { id: 456 }, { id: 9280 }];
        const pointscoredCollection: IPointscored[] = [{ id: 123 }];
        expectedResult = service.addPointscoredToCollectionIfMissing(pointscoredCollection, ...pointscoredArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pointscored: IPointscored = { id: 123 };
        const pointscored2: IPointscored = { id: 456 };
        expectedResult = service.addPointscoredToCollectionIfMissing([], pointscored, pointscored2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pointscored);
        expect(expectedResult).toContain(pointscored2);
      });

      it('should accept null and undefined values', () => {
        const pointscored: IPointscored = { id: 123 };
        expectedResult = service.addPointscoredToCollectionIfMissing([], null, pointscored, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pointscored);
      });

      it('should return initial array if no Pointscored is added', () => {
        const pointscoredCollection: IPointscored[] = [{ id: 123 }];
        expectedResult = service.addPointscoredToCollectionIfMissing(pointscoredCollection, undefined, null);
        expect(expectedResult).toEqual(pointscoredCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
