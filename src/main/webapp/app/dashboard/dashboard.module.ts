import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DASHBOARD_ROUTE } from './dashboard.route';
import { SharedModule } from 'app/shared/shared.module';
import { DashboardComponent } from './dashboard.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([DASHBOARD_ROUTE])],
  declarations: [DashboardComponent],
})
export class DashboardModule {}
