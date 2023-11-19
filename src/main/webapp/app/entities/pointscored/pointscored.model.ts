import { ITasks } from 'app/entities/tasks/tasks.model';

export interface IPointscored {
  id?: number;
  name?: ITasks | null;
}

export class Pointscored implements IPointscored {
  constructor(public id?: number, public name?: ITasks | null) {}
}

export function getPointscoredIdentifier(pointscored: IPointscored): number | undefined {
  return pointscored.id;
}
