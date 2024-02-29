export interface IUser {
  id: string;
  login?: string;
  isOnline?: boolean;
}

export class User implements IUser {
  constructor(
    public id: string,
    public login: string,
    public isOnline: boolean,
  ) {}
}

export function getUserIdentifier(user: IUser): string {
  return user.id;
}
