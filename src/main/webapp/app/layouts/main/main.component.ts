import { Component, HostListener, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';

import { AccountService } from 'app/core/auth/account.service';
import { AppPageTitleStrategy } from 'app/app-page-title-strategy';
import FooterComponent from '../footer/footer.component';
import PageRibbonComponent from '../profiles/page-ribbon.component';
import { StompService } from 'app/shared/service/stomp.service';
import { IUser } from 'app/entities/user/user.model';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-main',
  standalone: true,
  templateUrl: './main.component.html',
  providers: [AppPageTitleStrategy],
  imports: [RouterOutlet, FooterComponent, PageRibbonComponent],
})
export default class MainComponent implements OnInit {
  user: IUser | null = null;
  private isRefreshing = false;
  constructor(
    private router: Router,
    private appPageTitleStrategy: AppPageTitleStrategy,
    private accountService: AccountService,
    private stompService: StompService, 
  ) {}

  ngOnInit(): void {
    // try to log in automatically
    this.accountService.identity().subscribe();

    this.accountService
      .getAuthenticationState()
      .subscribe(account => {
        if(account) {
          this.user = {
            id: '',
            login: account.login,
          }
          this.stompService.send('/app/user.connect', {}, JSON.stringify(this.user));
        }
      });
  }

  @HostListener('window:beforeunload', ['$event'])
  onBeforeUnload(event: Event): void {
    this.isRefreshing = true;
  }

  @HostListener('window:unload', ['$event'])
  disconnectUser(event: Event): void {
    if(this.user && !this.isRefreshing) {
      this.stompService.send('/app/user.disconnect', {}, JSON.stringify(this.user));
    }
  }
}
