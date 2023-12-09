import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPointscored } from '../pointscored.model';

@Component({
  selector: 'jhi-pointscored-detail',
  templateUrl: './pointscored-detail.component.html',
})
export class PointscoredDetailComponent implements OnInit {
  pointscored: IPointscored | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pointscored }) => {
      this.pointscored = pointscored;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
