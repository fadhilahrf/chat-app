import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMessage, NewMessage } from '../message.model';

export type PartialUpdateMessage = Partial<IMessage> & Pick<IMessage, 'id'>;

export type EntityResponseType = HttpResponse<IMessage>;
export type EntityArrayResponseType = HttpResponse<IMessage[]>;

@Injectable({ providedIn: 'root' })
export class MessageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/messages');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(message: NewMessage): Observable<EntityResponseType> {
    return this.http.post<IMessage>(this.resourceUrl, message, { observe: 'response' });
  }

  update(message: IMessage): Observable<EntityResponseType> {
    return this.http.put<IMessage>(`${this.resourceUrl}/${this.getMessageIdentifier(message)}`, message, { observe: 'response' });
  }

  partialUpdate(message: PartialUpdateMessage): Observable<EntityResponseType> {
    return this.http.patch<IMessage>(`${this.resourceUrl}/${this.getMessageIdentifier(message)}`, message, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IMessage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAllByRecipient(recipient: string): Observable<EntityArrayResponseType> {
    return this.http.get<IMessage[]>(`${this.resourceUrl}/user/${recipient}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMessage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMessageIdentifier(message: Pick<IMessage, 'id'>): string {
    return message.id;
  }

  compareMessage(o1: Pick<IMessage, 'id'> | null, o2: Pick<IMessage, 'id'> | null): boolean {
    return o1 && o2 ? this.getMessageIdentifier(o1) === this.getMessageIdentifier(o2) : o1 === o2;
  }

  addMessageToCollectionIfMissing<Type extends Pick<IMessage, 'id'>>(
    messageCollection: Type[],
    ...messagesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const messages: Type[] = messagesToCheck.filter(isPresent);
    if (messages.length > 0) {
      const messageCollectionIdentifiers = messageCollection.map(messageItem => this.getMessageIdentifier(messageItem)!);
      const messagesToAdd = messages.filter(messageItem => {
        const messageIdentifier = this.getMessageIdentifier(messageItem);
        if (messageCollectionIdentifiers.includes(messageIdentifier)) {
          return false;
        }
        messageCollectionIdentifiers.push(messageIdentifier);
        return true;
      });
      return [...messagesToAdd, ...messageCollection];
    }
    return messageCollection;
  }
}
