import { Component, ViewEncapsulation } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-message-delete-dialog',
  standalone: true,
  imports: [SharedModule, FormsModule],
  templateUrl: './message-delete-dialog.component.html',
  styleUrl: './message-delete-dialog.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class MessageDeleteDialogComponent {
  message?: IMessage;
  isForAll?: boolean;

  deleteMessageConfirmation = {
    FOR_YOU: 'Are you sure you want to delete this message just for you?',
    FOR_ALL: 'Are you sure you want to delete this message for all?'
  }

  constructor(
    protected messageService: MessageService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(): void {
    this.activeModal.close();
  }
}
