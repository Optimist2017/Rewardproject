import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'jhi-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  items = [
    { title: 'Exploring Angular 12 Features', content: 'In this video, we explore the latest features in Angular 12.', link: 'https://www.youtube.com/watch?v=pq34V_V5j18', likes: 1200 },
    { title: 'React State Management Techniques', content: 'Learn different state management techniques in React.', link: 'https://www.youtube.com/watch?v=abc', likes: 800 },
    { title: 'Vue.js Crash Course for Beginners', content: 'A beginner-friendly crash course on Vue.js framework.', link: 'https://www.youtube.com/watch?v=pqr', likes: 1500 },
    { title: 'Building RESTful APIs with Spring Boot', content: 'Create RESTful APIs using Spring Boot and Java.', link: 'https://www.youtube.com/watch?v=lmn', likes: 2000 },
    { title: 'Python Web Scraping Tutorial', content: 'Learn how to scrape websites using Python and BeautifulSoup.', link: 'https://www.youtube.com/watch?v=def', likes: 600 },
    { title: 'Machine Learning Basics with Scikit-Learn', content: 'Introduction to machine learning using Scikit-Learn in Python.', link: 'https://www.youtube.com/watch?v=jkl', likes: 1300 },
    // Add more items as needed
  ];

  itemd = [
    { title: 'CSS Grid Layout Tutorial', content: 'Master the CSS Grid layout for building responsive web designs.', link: 'https://www.youtube.com/watch?v=mno', likes: 1100 },
    { title: 'Dockerize Your Node.js App', content: 'Containerize your Node.js application with Docker for deployment.', link: 'https://www.youtube.com/watch?v=ghi', likes: 900 },

  ];
  activeTab = 'new-tasks';
  account: Account | null = null;
  private readonly destroy$ = new Subject<void>();
  constructor(private router: Router, private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    this.accountService.identity().subscribe(() => {
      if (this.accountService.isAuthenticated()) {
        this.router.navigate(['dashboard']);
      }
    });
  }



  showTab(tab: string): void {
    this.activeTab = tab;
  }

  watch(selectedItem:any): void {
    const navigationExtras = {
      state: {
        youtubeUrl: selectedItem.link
      },
    };
    this.router.navigate(['/dashboard/watch'],navigationExtras);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
