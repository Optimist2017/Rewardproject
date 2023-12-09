import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPointscored, Pointscored } from '../pointscored.model';
import { PointscoredService } from '../service/pointscored.service';

@Injectable({ providedIn: 'root' })
export class PointscoredRoutingResolveService implements Resolve<IPointscored> {
  constructor(protected service: PointscoredService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPointscored> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pointscored: HttpResponse<Pointscored>) => {
          if (pointscored.body) {
            return of(pointscored.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pointscored());
  }
}
