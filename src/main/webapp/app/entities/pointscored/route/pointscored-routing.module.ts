import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PointscoredComponent } from '../list/pointscored.component';
import { PointscoredDetailComponent } from '../detail/pointscored-detail.component';
import { PointscoredUpdateComponent } from '../update/pointscored-update.component';
import { PointscoredRoutingResolveService } from './pointscored-routing-resolve.service';

const pointscoredRoute: Routes = [
  {
    path: '',
    component: PointscoredComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PointscoredDetailComponent,
    resolve: {
      pointscored: PointscoredRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PointscoredUpdateComponent,
    resolve: {
      pointscored: PointscoredRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PointscoredUpdateComponent,
    resolve: {
      pointscored: PointscoredRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(pointscoredRoute)],
  exports: [RouterModule],
})
export class PointscoredRoutingModule {}
