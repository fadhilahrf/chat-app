import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMessage } from '../message.model';
import { MessageService } from '../service/message.service';

@Component({
  standalone: true,
  templateUrl: './message-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MessageDeleteDialogComponent {
  message?: IMessage;

  constructor(
    protected messageService: MessageService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.messageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
