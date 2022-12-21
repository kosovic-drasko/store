import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../products.test-samples';

import { ProductsFormService } from './products-form.service';

describe('Products Form Service', () => {
  let service: ProductsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductsFormService);
  });

  describe('Service methods', () => {
    describe('createProductsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            articalName: expect.any(Object),
            articalPrice: expect.any(Object),
          })
        );
      });

      it('passing IProducts should create a new form with FormGroup', () => {
        const formGroup = service.createProductsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            articalName: expect.any(Object),
            articalPrice: expect.any(Object),
          })
        );
      });
    });

    describe('getProducts', () => {
      it('should return NewProducts for default Products initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProductsFormGroup(sampleWithNewData);

        const products = service.getProducts(formGroup) as any;

        expect(products).toMatchObject(sampleWithNewData);
      });

      it('should return NewProducts for empty Products initial value', () => {
        const formGroup = service.createProductsFormGroup();

        const products = service.getProducts(formGroup) as any;

        expect(products).toMatchObject({});
      });

      it('should return IProducts', () => {
        const formGroup = service.createProductsFormGroup(sampleWithRequiredData);

        const products = service.getProducts(formGroup) as any;

        expect(products).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProducts should not enable id FormControl', () => {
        const formGroup = service.createProductsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProducts should disable id FormControl', () => {
        const formGroup = service.createProductsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
