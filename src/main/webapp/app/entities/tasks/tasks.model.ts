export interface ITasks {
  id?: number;
  name?: string | null;
  url?: string | null;
  description?: string | null;
  point?: number | null;
}

export class Tasks implements ITasks {
  constructor(
    public id?: number,
    public name?: string | null,
    public url?: string | null,
    public description?: string | null,
    public point?: number | null
  ) {}
}

export function getTasksIdentifier(tasks: ITasks): number | undefined {
  return tasks.id;
}
