import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPointscored, Pointscored } from '../pointscored.model';
import { PointscoredService } from '../service/pointscored.service';
import { ITasks } from 'app/entities/tasks/tasks.model';
import { TasksService } from 'app/entities/tasks/service/tasks.service';

@Component({
  selector: 'jhi-pointscored-update',
  templateUrl: './pointscored-update.component.html',
})
export class PointscoredUpdateComponent implements OnInit {
  isSaving = false;

  tasksSharedCollection: ITasks[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
  });

  constructor(
    protected pointscoredService: PointscoredService,
    protected tasksService: TasksService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pointscored }) => {
      this.updateForm(pointscored);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pointscored = this.createFromForm();
    if (pointscored.id !== undefined) {
      this.subscribeToSaveResponse(this.pointscoredService.update(pointscored));
    } else {
      this.subscribeToSaveResponse(this.pointscoredService.create(pointscored));
    }
  }

  trackTasksById(_index: number, item: ITasks): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPointscored>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(pointscored: IPointscored): void {
    this.editForm.patchValue({
      id: pointscored.id,
      name: pointscored.name,
    });

    this.tasksSharedCollection = this.tasksService.addTasksToCollectionIfMissing(this.tasksSharedCollection, pointscored.name);
  }

  protected loadRelationshipsOptions(): void {
    this.tasksService
      .query()
      .pipe(map((res: HttpResponse<ITasks[]>) => res.body ?? []))
      .pipe(map((tasks: ITasks[]) => this.tasksService.addTasksToCollectionIfMissing(tasks, this.editForm.get('name')!.value)))
      .subscribe((tasks: ITasks[]) => (this.tasksSharedCollection = tasks));
  }

  protected createFromForm(): IPointscored {
    return {
      ...new Pointscored(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
    };
  }
}
