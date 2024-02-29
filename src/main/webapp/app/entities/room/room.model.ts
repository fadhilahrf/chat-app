export interface IRoom {
  id: string;
  user1?: string | null;
  user2?: string | null;
  deleted1?: boolean | null;
  deleted2?: boolean | null;
}

export type NewRoom = Omit<IRoom, 'id'> & { id: null };
