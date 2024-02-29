import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { MessageComponent } from './list/message.component';
import { MessageDetailComponent } from './detail/message-detail.component';
import { MessageUpdateComponent } from './update/message-update.component';
import MessageResolve from './route/message-routing-resolve.service';

const messageRoute: Routes = [
  {
    path: '',
    component: MessageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MessageDetailComponent,
    resolve: {
      message: MessageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MessageUpdateComponent,
    resolve: {
      message: MessageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MessageUpdateComponent,
    resolve: {
      message: MessageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default messageRoute;
