import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPointscored } from '../pointscored.model';
import { PointscoredService } from '../service/pointscored.service';

@Component({
  templateUrl: './pointscored-delete-dialog.component.html',
})
export class PointscoredDeleteDialogComponent {
  pointscored?: IPointscored;

  constructor(protected pointscoredService: PointscoredService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.pointscoredService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
