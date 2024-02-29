import { IMessage, NewMessage } from './message.model';

export const sampleWithRequiredData: IMessage = {
  id: '711fe8a9-36eb-4a0e-9b3b-2c6de5ac162b',
  sender: 'regulation',
  recipient: 'upset',
  content: 'urgently',
};

export const sampleWithPartialData: IMessage = {
  id: 'e0e6e00f-d673-4c79-bf9d-824e6688e110',
  sender: 'earnings',
  recipient: 'handmaiden anti closure',
  content: 'consequently lung',
};

export const sampleWithFullData: IMessage = {
  id: '61936786-12e1-4388-acfb-7a65244aa820',
  sender: 'surroundings jealously',
  recipient: 'when meager',
  content: 'commune cop-out gadzooks',
};

export const sampleWithNewData: NewMessage = {
  sender: 'ah',
  recipient: 'alongside abandoned',
  content: 'utter',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
