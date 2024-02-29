import { IRoom, NewRoom } from './room.model';

export const sampleWithRequiredData: IRoom = {
  id: 'f357d5e6-e6e3-4236-a470-b898a12baf80',
  user1: 'hm lest septicaemia',
  user2: 'masculinize inasmuch',
};

export const sampleWithPartialData: IRoom = {
  id: 'aa2060e4-35dc-47db-bc00-15a64496565a',
  user1: 'dredge',
  user2: 'quirkily rigidly',
  deleted2: false,
};

export const sampleWithFullData: IRoom = {
  id: 'd82b0e54-833a-4813-bbb1-6edd4a75b3b3',
  user1: 'finally troubled',
  user2: 'inquisitively lumberman',
  deleted1: true,
  deleted2: true,
};

export const sampleWithNewData: NewRoom = {
  user1: 'correctly nor unimpressively',
  user2: 'once encirclement',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
