import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import SharedModule from 'app/shared/shared.module';
import { StompService } from 'app/shared/service/stomp.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IGroupedMessage, IMessage, NewMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';
import dayjs from 'dayjs/esm';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';
import { NgbDropdown, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DeleteDialogComponent } from './dialogs/delete-dialog/delete-dialog.component';
import { DeleteConfirmation, DeleteType } from 'app/app.constants';
import { MessageEditDialogComponent } from './dialogs/message-edit-dialog/message-edit-dialog.component';
import { LoginService } from 'app/login/login.service';
import { Router, RouterModule } from '@angular/router';
import { timer } from 'rxjs';

@Component({
  selector: 'jhi-chat',
  standalone: true,
  imports: [SharedModule, RouterModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
})
export class ChatComponent implements OnInit {
  @ViewChild('chatMessages', { static: false }) private chatMessages?: ElementRef<HTMLElement>;

  @ViewChild('messageInput', { static: false }) private messageInput?: ElementRef<HTMLInputElement>;

  ctx = '';

  users: IUser[] = [];

  account: Account | null = null;

  messages: IMessage[] = [];

  rooms: IRoom[] = [];

  groupedMessages: IGroupedMessage[] = [];

  messageForm: NewMessage = { id: null };
 
  selectedRecipient: IUser | null = null;

  selectedMessageToReply: IMessage | null = null;

  isInputFocused: boolean = false;

  firstUnreadMessageId = '';

  $primary = '#0dcaf0';

  $info = '#0d6efd';

  unreadMessagesNumber = 0;

  screenWidth?: number;

  SCREEN_WIDTH_THRESHOLD = 450;

 showDropdown = false;

//  mouseDownTime = 0;

 clickDurationThreshold: number = 1000; 

  constructor(
    private userService: UserService, 
    private accountService: AccountService, 
    private roomService: RoomService, 
    private messageService: MessageService, 
    private stompService: StompService, 
    private modalService: NgbModal,
    private loginService: LoginService,
    private router: Router,
    private elementRef: ElementRef
  ) {
    this.getScreenSize();
  }

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      this.getRooms();
    });

    this.stompService.connect({}, ()=>{
      this.stompService.getStomp().subscribe('/topic/public/connection', (payload)=>{
        try {
          if(JSON.parse(payload.body)) {
            const user: IUser = JSON.parse(payload.body);
            if(user?.login! == this.selectedRecipient?.login) {
              this.selectedRecipient = user;
            }
          }
      } catch (error) {
          if (error instanceof TypeError) {
              console.error("Caught a TypeError:", error.message);
          } else {
              throw error;
          }
      }
      });

      this.stompService.getStomp().subscribe(`/user/${this.account!.login}/notification/messages`, (payload)=>{
        this.getMessages(this.selectedRecipient!, false);
        this.getRooms();
      });

      this.stompService.getStomp().subscribe(`/user/${this.account!.login}/messages/read`, (payload)=>{
        try {
          if(JSON.parse(payload.body)) {
            const messages: IMessage[] = JSON.parse(payload.body);
            
            messages.forEach(message=>{
              this.groupedMessages.forEach(groupedMessage=>{
                groupedMessage.messages.forEach(m=>{
                    if(m.id==message.id) {
                      m.read = message.read;
                      if(this.unreadMessagesNumber>0) {
                        this.unreadMessagesNumber--;
                      }
                      this.updateRoomLatestMessage(message.roomId!);
                    }
                })
              });
            });
          }
      } catch (error) {
          if (error instanceof TypeError) {
              console.error("Caught a TypeError:", error.message);
          } else {
              throw error;
          }
      }
      });

      this.stompService.getStomp().subscribe(`/user/${this.account!.login}/message/edited`, (payload)=>{
        try {
          if(JSON.parse(payload.body)) {
            const message: IMessage = JSON.parse(payload.body);
            
            this.groupedMessages.forEach(groupedMessage=>{
              groupedMessage.messages.forEach(m=>{
                if (m.id === message.id) {
                  m.isEdited = message.isEdited;
                  m.content = message.content;
                  this.updateRoomLatestMessage(message.roomId!);
                }

                if(m.replyTo && m.replyTo.id==message.id) {
                  m.replyTo.isEdited = message.isEdited;
                  m.replyTo.content = message.content;
                }
              })
            });
          }
      } catch (error) {
          if (error instanceof TypeError) {
              console.error("Caught a TypeError:", error.message);
          } else {
              throw error;
          }
      }
      });

      this.stompService.getStomp().subscribe(`/user/${this.account!.login}/message/deleted`, (payload)=>{
        try {
          if(JSON.parse(payload.body)) {
            const message: IMessage = JSON.parse(payload.body);
            
            this.groupedMessages.forEach(groupedMessage=>{
              groupedMessage.messages.forEach((item, index, object)=>{
                if (item.id === message.id) {
                  object.splice(index, 1);
                  if(this.unreadMessagesNumber>0) {
                    this.unreadMessagesNumber--;
                  }
                  this.updateRoomLatestMessage(message.roomId!);
                }

                if(item.replyTo && item.replyTo.id == message.id) {
                  item.replyTo.isDeleted = true;
                }
              })
            });
          }
      } catch (error) {
          if (error instanceof TypeError) {
              console.error("Caught a TypeError:", error.message);
          } else {
              throw error;
          }
      }
      });

      this.stompService.getStomp().subscribe(`/user/${this.account!.login}/room/deleted`, (payload)=>{
        try {
          if(JSON.parse(payload.body)) {
            const room: IRoom = JSON.parse(payload.body);
            
            if((this.selectedRecipient?.login==room.user1 || this.selectedRecipient?.login==room.user2) && this.selectedRecipient?.login!=this.account!.login) {
              this.selectedRecipient = null;
            }

            this.rooms.forEach((item, index, object)=>{
              if (item.id === room.id) {
                object.splice(index, 1);

                if(room.user1==this.account?.login) {
                  this.unreadMessagesNumber=this.unreadMessagesNumber-item.unreadMessagesNumber1!;
                }else {
                  this.unreadMessagesNumber=this.unreadMessagesNumber-item.unreadMessagesNumber2!;
                }
              }
            })
          }
      } catch (error) {
          if (error instanceof TypeError) {
              console.error("Caught a TypeError:", error.message);
          } else {
              throw error;
          }
      }
      });
    }, ()=>{console.log("error")})
  }

  getUsers(): void{
    this.userService.getAll().subscribe(res=>{
      if(res.body) {
        this.users = res.body.filter(user=>user.login!=this.account?.login);
      }
    })
  }

  searchUsers(event: any): void {
    const search = event.target.value;
    if(search!='') {
      this.userService.getAll(search).subscribe(res=>{
        if(res.body) {
          this.users = res.body.filter(user=>user.login!=this.account?.login);
        }
      })
    }else {
      this.users = [];
    }
  }

  getRecipientChat(login: string): void {
    this.userService.getByLogin(login).subscribe(res=>{
      if(res.body) {
        this.selectedRecipient = res.body;
        this.getMessages(this.selectedRecipient, true);
      }
    })
  }

  getRooms(): void {
    this.roomService.getAllRoomsForUserSortedByLatestMessageTime().subscribe(res=>{
      if(res.body) {
        this.rooms = res.body;
        this.unreadMessagesNumber=0;
        this.rooms.forEach(room=>{
          if(room.user1==this.account?.login) {
            this.unreadMessagesNumber+=room.unreadMessagesNumber1!;
          }else {
            this.unreadMessagesNumber+=room.unreadMessagesNumber2!;
          }
        })
      }
    });
  }

  updateRoomLatestMessage(id: string): void {
    this.roomService.find(id).subscribe(res=>{
      if(res.body) {
        this.rooms.forEach(room=>{
          if(res.body?.id == room.id) {
            room.latestMessage = res.body.latestMessage;
          }
        })
      }
    });
  }

  getMessages(recipient: IUser, firstInit: boolean): void {
    this.selectedRecipient = recipient;
    if(this.selectedRecipient) {
      this.messageService.getAllByRecipient(this.selectedRecipient.login!).subscribe(res=>{
        if(res.body) {
          this.messages = res.body;
          this.groupedMessages = [];
          const distinctDate = new Set(this.messages.map(m=>this.getDate(m.deliveryTime!.toString())));
          const unreadMessages: IMessage[] = [];
          let isFirstUnreadMessage = true; 
          distinctDate.forEach(d=>{
              const groupedMessage: IGroupedMessage = {
                date: d,
                messages: this.messages.filter(m=>this.getDate(m.deliveryTime!.toString())==d).map(m=>{
                    if(!m.read && this.account?.login == m.recipient) {
                      if(isFirstUnreadMessage) {
                        this.firstUnreadMessageId = m.id;
                        isFirstUnreadMessage = false;
                      }
                      m.read = true;
                      unreadMessages.push(m);
                    }
                  
                  return m;
                })
              }
              this.stompService.send(`/app/${this.selectedRecipient?.login}/messages/read`, {}, JSON.stringify(unreadMessages));
              this.groupedMessages.push(groupedMessage);
              setTimeout(()=>{
                this.getRooms();
              }, 100);
              
              if(firstInit) {
                setTimeout(()=>{
                  this.scrollToBottom();
                }, 100);
              }
          })
        }
      });
    }
  }

  sendMessage(): void {
    if (this.messageForm.content && this.selectedRecipient) {
      this.messageForm.sender = this.account!.login;
      this.messageForm.recipient = this.selectedRecipient.login;
      this.messageForm.read = false;
      if(this.selectedMessageToReply) {
        this.messageForm.replyTo = this.selectedMessageToReply;
        this.selectedMessageToReply = null;
      }
      this.stompService.send('/app/message', {}, JSON.stringify(this.messageForm));
      this.messageForm.content = null;
      this.getMessages(this.selectedRecipient, true);
      this.getRooms();
    }
  }

  getTime(date: string): string {
    const dateTime = dayjs(date);

    return dateTime.format('HH.mm');
  }

  getDate(date: string): string {
    const dateTime = dayjs(date);

    return dateTime.format('D MMMM YYYY');
  }

  getRoomRecipient(room: IRoom): string {
    return room.user1!=this.account?.login ? room.user1! : room.user2!;
  }

  scrollToBottom(): void {
    if(this.firstUnreadMessageId) {
      const targetElement = <HTMLElement> document.getElementById(this.firstUnreadMessageId);
      this.chatMessages!.nativeElement.scrollTop = targetElement.offsetTop-150;
    }else {
    this.chatMessages!.nativeElement.scrollTop = this.chatMessages!.nativeElement.scrollHeight;
    }
  }

  scrollToRepliedMessage(id: string): void {
    const targetElement = <HTMLElement> document.getElementById(id);
    this.chatMessages!.nativeElement.scrollTop = this.chatMessages!.nativeElement.scrollHeight;
  }

  onSearchInputFocused(): void {
    this.isInputFocused = true;
  }

  leaveSearch(): void {
    this.isInputFocused = false;
    (document.getElementById('search') as HTMLInputElement).value = ''
  }

  getUnreadMessagesNumber(room: IRoom): number | null | undefined {
      if(this.account?.login==room.user1) {
        return room.unreadMessagesNumber1;
      }else {
        return room.unreadMessagesNumber2;
      }
  }

  deleteMessage(message: IMessage, isForAll: boolean): void {
    const modalRef = this.modalService.open(DeleteDialogComponent, { size: 'md', centered: true });
    modalRef.componentInstance.entity = message;
    modalRef.componentInstance.type = DeleteType.MESSAGE;
    if(isForAll) {
      modalRef.componentInstance.confirmation = DeleteConfirmation.FOR_ALL_MESSAGE;
    }else {
      modalRef.componentInstance.confirmation = DeleteConfirmation.FOR_YOU_MESSAGE;
    }

    modalRef.closed.subscribe(state=>{
      if(isForAll){
        this.stompService.send(`/app/message/delete/${message.id}/all-users`, {}, JSON.stringify({}));
      }else {
        this.stompService.send(`/app/message/delete/${message.id}/user/${this.account!.login}`, {}, JSON.stringify({}));
      }
    });
  }

  editMessage(message: IMessage): void {
    const modalRef = this.modalService.open(MessageEditDialogComponent, { size: 'md', centered: true });
    modalRef.componentInstance.prevMessageContent = message.content;

    modalRef.componentInstance.editedMessage.subscribe((newMessageContent: string)=>{
      if(message.content!=newMessageContent) {
        message.content=newMessageContent;
        this.stompService.send('/app/message/edit', {}, JSON.stringify(message));
      }
    });
  }

  deleteRoom(room: IRoom, isForAll: boolean): void {
    const modalRef = this.modalService.open(DeleteDialogComponent, { size: 'md', centered: true });
    modalRef.componentInstance.entity = room;
    modalRef.componentInstance.type = DeleteType.ROOM;
    if(isForAll) {
      modalRef.componentInstance.confirmation = DeleteConfirmation.FOR_ALL_ROOM;
    }else {
      modalRef.componentInstance.confirmation = DeleteConfirmation.FOR_YOU_ROOM;
    }

    modalRef.closed.subscribe(state=>{
      if(isForAll){
        this.stompService.send(`/app/room/delete/${room.id}/all-users`, {}, JSON.stringify({}));
      }else {
        this.stompService.send(`/app/room/delete/${room.id}/user/${this.account!.login}`, {}, JSON.stringify({}));
      }
    });
  }

  countNotDeletedMessages(messages: IMessage[]): number {
    return messages.filter(m=>!m.isDeleted).length;
  }

  selectMessageToReply(message: IMessage): void {
    this.selectedMessageToReply = message;
    this.messageInput?.nativeElement.focus();
  }

  removeSelectedMessageToReply(): void {
    this.selectedMessageToReply = null;
  }

  logout(): void {
    const user: IUser = {
      id: '',
      login: this.account?.login,
    }
    this.loginService.logout();
    this.stompService.send('/app/user.disconnect', {}, JSON.stringify(user));
    this.router.navigate(['']);
  }

  @HostListener('window:resize', ['$event'])
  private getScreenSize(): void {
    this.screenWidth = window.innerWidth;
  }

  backToChatList(): void {
    this.selectedRecipient = null;
  }
}
