import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MessageDetailComponent } from './message-detail.component';

describe('Message Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MessageDetailComponent,
              resolve: { message: () => of({ id: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MessageDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load message on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MessageDetailComponent);

      // THEN
      expect(instance.message).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
