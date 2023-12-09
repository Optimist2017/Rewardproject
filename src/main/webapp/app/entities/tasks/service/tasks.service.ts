import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITasks, getTasksIdentifier } from '../tasks.model';

export type EntityResponseType = HttpResponse<ITasks>;
export type EntityArrayResponseType = HttpResponse<ITasks[]>;

@Injectable({ providedIn: 'root' })
export class TasksService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tasks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tasks: ITasks): Observable<EntityResponseType> {
    return this.http.post<ITasks>(this.resourceUrl, tasks, { observe: 'response' });
  }

  update(tasks: ITasks): Observable<EntityResponseType> {
    return this.http.put<ITasks>(`${this.resourceUrl}/${getTasksIdentifier(tasks) as number}`, tasks, { observe: 'response' });
  }

  partialUpdate(tasks: ITasks): Observable<EntityResponseType> {
    return this.http.patch<ITasks>(`${this.resourceUrl}/${getTasksIdentifier(tasks) as number}`, tasks, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITasks>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITasks[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTasksToCollectionIfMissing(tasksCollection: ITasks[], ...tasksToCheck: (ITasks | null | undefined)[]): ITasks[] {
    const tasks: ITasks[] = tasksToCheck.filter(isPresent);
    if (tasks.length > 0) {
      const tasksCollectionIdentifiers = tasksCollection.map(tasksItem => getTasksIdentifier(tasksItem)!);
      const tasksToAdd = tasks.filter(tasksItem => {
        const tasksIdentifier = getTasksIdentifier(tasksItem);
        if (tasksIdentifier == null || tasksCollectionIdentifiers.includes(tasksIdentifier)) {
          return false;
        }
        tasksCollectionIdentifiers.push(tasksIdentifier);
        return true;
      });
      return [...tasksToAdd, ...tasksCollection];
    }
    return tasksCollection;
  }
}
