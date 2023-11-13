import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
@Component({
  selector: 'jhi-watch',
  templateUrl: './watch.component.html',
  styleUrls: ['./watch.component.scss'],
})
export class WatchComponent  implements OnInit {
  youtubeUrl: string | any;

  constructor(private route: ActivatedRoute) {}
  

    ngOnInit(): void {
      // Subscribe to route changes to get the latest state
      this.route.paramMap.pipe(take(1)).subscribe((params) => {
        // Retrieve the data from the route
        this.youtubeUrl = params.get('youtubeUrl');

      });
     
  }
  
}
