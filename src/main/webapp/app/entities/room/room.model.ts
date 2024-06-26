import { IMessage } from "../message/message.model";

export interface IRoom {
  id: string;
  user1?: string | null;
  user2?: string | null;
  unreadMessagesNumber1?: number | null;
  unreadMessagesNumber2?: number | null;
  deleted1?: boolean | null;
  deleted2?: boolean | null;
  deletedBy?: string | null;
  latestMessage?: IMessage | null;
}

export type NewRoom = Omit<IRoom, 'id'> & { id: null };
