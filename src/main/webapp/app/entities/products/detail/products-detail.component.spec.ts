import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductsDetailComponent } from './products-detail.component';

describe('Products Management Detail Component', () => {
  let comp: ProductsDetailComponent;
  let fixture: ComponentFixture<ProductsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ products: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load products on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.products).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
