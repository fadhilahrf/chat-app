import { IRoom } from 'app/entities/room/room.model';
import dayjs from 'dayjs/esm';

export interface IMessage {
  id: string;
  sender?: string | null;
  recipient?: string | null;
  content?: string | null;
  roomId?: string | null;
  deliveryTime?: dayjs.Dayjs | null;
  read?: boolean | null;
  isDeleted?: boolean | null;
  isEdited?: boolean | null;
  replyTo?: IMessage | null;
}

export interface IGroupedMessage  { 
  date: string; 
  messages: IMessage[];
}

export type NewMessage = Omit<IMessage, 'id'> & { id: null };
