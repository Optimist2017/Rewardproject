import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { Observable, Subject } from 'rxjs';
import { takeUntil, catchError, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'jhi-watch',
  templateUrl: './watch.component.html',
  styleUrls: ['./watch.component.scss'],
})
export class WatchComponent implements OnInit {
  trustedUrl: any;
  youtubeUrl: string | any;
  account: Account | null = null;
  videoId = 'VIDEO_ID';

  // Replace with your API key
  private apiKey = '';

  private destroy$ = new Subject<void>();

  constructor(
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    private http: HttpClient,
    private accountService: AccountService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    this.route.params.subscribe(params => {
      this.videoId = params['videoid'];
      console.warn(this.videoId);
      this.youtubeUrl = `https://www.youtube.com/embed/${this.videoId}`;
      this.trustedUrl = this.updateUrl(this.youtubeUrl);
      console.warn(this.trustedUrl);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // Send the API request
  getVideoViews(): Observable<number> {
    const url = `https://www.googleapis.com/youtube/v3/videos?part=statistics&id=${this.videoId}&key=${this.apiKey}`;

    return this.http.get(url).pipe(
      map((response: any) => response.items[0].statistics.viewCount as number),
      catchError((error: any) => {
        console.error(error);
        throw error; // Rethrow the error for the subscriber
      })
    );
  }

  updateUrl(youtubeUrl: string): any {
    const rightUrl = this.sanitizer.bypassSecurityTrustUrl(youtubeUrl);
    return rightUrl;
  }
}
