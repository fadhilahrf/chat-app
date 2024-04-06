import { Component, EventEmitter, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IMessage } from 'app/entities/message/message.model';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-message-edit-dialog',
  standalone: true,
  imports: [SharedModule, FormsModule],
  templateUrl: './message-edit-dialog.component.html',
  styleUrl: './message-edit-dialog.component.scss',
  encapsulation: ViewEncapsulation.None
})
export class MessageEditDialogComponent implements OnInit{
  @Output() editedMessage: EventEmitter<any> = new EventEmitter();
  prevMessageContent="";
  newMessageContent="";
  messageForm = this.fb.group({
    content: [''],
  });

  constructor(
    protected activeModal: NgbActiveModal, 
    protected fb: FormBuilder
  ) {
  }

  ngOnInit(): void {
      this.newMessageContent = this.prevMessageContent;
  }

  cancel(): void {
    this.activeModal.dismiss();
  }

  save(): void {
    this.editedMessage.emit(this.newMessageContent);
    this.activeModal.close();
  }
}
