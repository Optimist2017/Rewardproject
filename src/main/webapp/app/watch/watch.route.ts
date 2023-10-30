import { Route } from '@angular/router';

import { WatchComponent } from './watch.component';

export const WATCH_ROUTE: Route = {
  path: '',
  component: WatchComponent,
  data: {
    pageTitle: 'dashboard.watch',
  },
};
