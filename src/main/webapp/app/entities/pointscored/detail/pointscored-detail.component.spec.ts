import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PointscoredDetailComponent } from './pointscored-detail.component';

describe('Pointscored Management Detail Component', () => {
  let comp: PointscoredDetailComponent;
  let fixture: ComponentFixture<PointscoredDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PointscoredDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ pointscored: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PointscoredDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PointscoredDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load pointscored on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.pointscored).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
