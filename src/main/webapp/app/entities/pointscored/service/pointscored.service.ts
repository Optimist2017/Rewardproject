import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPointscored, getPointscoredIdentifier } from '../pointscored.model';

export type EntityResponseType = HttpResponse<IPointscored>;
export type EntityArrayResponseType = HttpResponse<IPointscored[]>;

@Injectable({ providedIn: 'root' })
export class PointscoredService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pointscoreds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pointscored: IPointscored): Observable<EntityResponseType> {
    return this.http.post<IPointscored>(this.resourceUrl, pointscored, { observe: 'response' });
  }

  update(pointscored: IPointscored): Observable<EntityResponseType> {
    return this.http.put<IPointscored>(`${this.resourceUrl}/${getPointscoredIdentifier(pointscored) as number}`, pointscored, {
      observe: 'response',
    });
  }

  partialUpdate(pointscored: IPointscored): Observable<EntityResponseType> {
    return this.http.patch<IPointscored>(`${this.resourceUrl}/${getPointscoredIdentifier(pointscored) as number}`, pointscored, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPointscored>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPointscored[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPointscoredToCollectionIfMissing(
    pointscoredCollection: IPointscored[],
    ...pointscoredsToCheck: (IPointscored | null | undefined)[]
  ): IPointscored[] {
    const pointscoreds: IPointscored[] = pointscoredsToCheck.filter(isPresent);
    if (pointscoreds.length > 0) {
      const pointscoredCollectionIdentifiers = pointscoredCollection.map(pointscoredItem => getPointscoredIdentifier(pointscoredItem)!);
      const pointscoredsToAdd = pointscoreds.filter(pointscoredItem => {
        const pointscoredIdentifier = getPointscoredIdentifier(pointscoredItem);
        if (pointscoredIdentifier == null || pointscoredCollectionIdentifiers.includes(pointscoredIdentifier)) {
          return false;
        }
        pointscoredCollectionIdentifiers.push(pointscoredIdentifier);
        return true;
      });
      return [...pointscoredsToAdd, ...pointscoredCollection];
    }
    return pointscoredCollection;
  }
}
