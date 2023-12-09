import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'tasks',
        data: { pageTitle: 'rewardprojectApp.tasks.home.title' },
        loadChildren: () => import('./tasks/tasks.module').then(m => m.TasksModule),
      },
      {
        path: 'pointscored',
        data: { pageTitle: 'rewardprojectApp.pointscored.home.title' },
        loadChildren: () => import('./pointscored/pointscored.module').then(m => m.PointscoredModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
