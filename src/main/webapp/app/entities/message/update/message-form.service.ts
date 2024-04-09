import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMessage, NewMessage } from '../message.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMessage for edit and NewMessageFormGroupInput for create.
 */
type MessageFormGroupInput = IMessage | PartialWithRequiredKeyOf<NewMessage>;

type MessageFormDefaults = Pick<NewMessage, 'id'>;

type MessageFormGroupContent = {
  id: FormControl<IMessage['id'] | NewMessage['id']>;
  sender: FormControl<IMessage['sender']>;
  recipient: FormControl<IMessage['recipient']>;
  content: FormControl<IMessage['content']>;
  roomId: FormControl<IMessage['roomId']>;
};

export type MessageFormGroup = FormGroup<MessageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MessageFormService {
  createMessageFormGroup(message: MessageFormGroupInput = { id: null }): MessageFormGroup {
    const messageRawValue = {
      ...this.getFormDefaults(),
      ...message,
    };
    return new FormGroup<MessageFormGroupContent>({
      id: new FormControl(
        { value: messageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sender: new FormControl(messageRawValue.sender, {
        validators: [Validators.required],
      }),
      recipient: new FormControl(messageRawValue.recipient, {
        validators: [Validators.required],
      }),
      content: new FormControl(messageRawValue.content, {
        validators: [Validators.required],
      }),
      roomId: new FormControl(messageRawValue.roomId),
    });
  }

  getMessage(form: MessageFormGroup): IMessage | NewMessage {
    return form.getRawValue() as IMessage | NewMessage;
  }

  resetForm(form: MessageFormGroup, message: MessageFormGroupInput): void {
    const messageRawValue = { ...this.getFormDefaults(), ...message };
    form.reset(
      {
        ...messageRawValue,
        id: { value: messageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MessageFormDefaults {
    return {
      id: null,
    };
  }
}
