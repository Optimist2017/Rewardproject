import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { WATCH_ROUTE } from './watch.route';
import { SharedModule } from 'app/shared/shared.module';
import { WatchComponent } from './watch.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([WATCH_ROUTE])],
  declarations: [WatchComponent],
})
export class WatchModule {}
