import { Component, ViewEncapsulation } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteType } from 'app/app.constants';
import { MessageService } from 'app/entities/message/service/message.service';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'delete-dialog',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './delete-dialog.component.html',
  styleUrl: './delete-dialog.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class DeleteDialogComponent {
  entity?: any;
  confirmation="";
  type="";
  
  DeleteType = DeleteType;

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
