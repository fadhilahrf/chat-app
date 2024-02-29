import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'message',
    data: { pageTitle: 'Messages' },
    loadChildren: () => import('./message/message.routes'),
  },
  {
    path: 'room',
    data: { pageTitle: 'Rooms' },
    loadChildren: () => import('./room/room.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
