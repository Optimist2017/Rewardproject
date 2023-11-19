import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PointscoredComponent } from './list/pointscored.component';
import { PointscoredDetailComponent } from './detail/pointscored-detail.component';
import { PointscoredUpdateComponent } from './update/pointscored-update.component';
import { PointscoredDeleteDialogComponent } from './delete/pointscored-delete-dialog.component';
import { PointscoredRoutingModule } from './route/pointscored-routing.module';

@NgModule({
  imports: [SharedModule, PointscoredRoutingModule],
  declarations: [PointscoredComponent, PointscoredDetailComponent, PointscoredUpdateComponent, PointscoredDeleteDialogComponent],
  entryComponents: [PointscoredDeleteDialogComponent],
})
export class PointscoredModule {}
