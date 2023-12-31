import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITasks } from '../tasks.model';
import { TasksService } from '../service/tasks.service';

@Component({
  templateUrl: './tasks-delete-dialog.component.html',
})
export class TasksDeleteDialogComponent {
  tasks?: ITasks;

  constructor(protected tasksService: TasksService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tasksService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
