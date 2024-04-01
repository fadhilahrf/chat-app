import { AfterViewChecked, AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import SharedModule from 'app/shared/shared.module';
import { StompService } from 'app/shared/service/stomp.service';
import { IUser, User } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IGroupedMessage, IMessage, NewMessage } from 'app/entities/message/message.model';
import { MessageService } from 'app/entities/message/service/message.service';
import dayjs from 'dayjs/esm';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';

@Component({
  selector: 'jhi-chat',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
})
export class ChatComponent implements OnInit {
  @ViewChild('chatMessages', { static: false }) private chatMessages?: ElementRef<HTMLElement>;

  ctx = '';

  users: IUser[] = [];

  account: Account | null = null;

  messages: IMessage[] = [];

  rooms: IRoom[] = [];

  groupedMessages: IGroupedMessage[] = [];

  messageForm: NewMessage = { id: null };
 
  selectedRecipient: IUser | null = null;

  isInputFocused: boolean = false;

  newMessageHeight = 0;

  firstUnreadMessageId = '';

  constructor(private userService: UserService, private accountService: AccountService, private roomService: RoomService, private messageService: MessageService, private stompService: StompService) {}

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
        this.newMessageHeight = 0;
        this.getMessages(this.selectedRecipient, true);
      }
    })
  }

  getRooms(): void {
    this.roomService.getAllRoomsForUserSortedByLatestMessageTime().subscribe(res=>{
      if(res.body) {
        this.rooms = res.body;
      }
    })
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
                      this.newMessageHeight+=50;
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
}
