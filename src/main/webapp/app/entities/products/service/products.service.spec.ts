import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProducts } from '../products.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../products.test-samples';

import { ProductsService } from './products.service';

const requireRestSample: IProducts = {
  ...sampleWithRequiredData,
};

describe('Products Service', () => {
  let service: ProductsService;
  let httpMock: HttpTestingController;
  let expectedResult: IProducts | IProducts[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Products', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const products = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(products).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Products', () => {
      const products = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(products).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Products', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Products', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Products', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductsToCollectionIfMissing', () => {
      it('should add a Products to an empty array', () => {
        const products: IProducts = sampleWithRequiredData;
        expectedResult = service.addProductsToCollectionIfMissing([], products);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(products);
      });

      it('should not add a Products to an array that contains it', () => {
        const products: IProducts = sampleWithRequiredData;
        const productsCollection: IProducts[] = [
          {
            ...products,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, products);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Products to an array that doesn't contain it", () => {
        const products: IProducts = sampleWithRequiredData;
        const productsCollection: IProducts[] = [sampleWithPartialData];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, products);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(products);
      });

      it('should add only unique Products to an array', () => {
        const productsArray: IProducts[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const productsCollection: IProducts[] = [sampleWithRequiredData];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, ...productsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const products: IProducts = sampleWithRequiredData;
        const products2: IProducts = sampleWithPartialData;
        expectedResult = service.addProductsToCollectionIfMissing([], products, products2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(products);
        expect(expectedResult).toContain(products2);
      });

      it('should accept null and undefined values', () => {
        const products: IProducts = sampleWithRequiredData;
        expectedResult = service.addProductsToCollectionIfMissing([], null, products, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(products);
      });

      it('should return initial array if no Products is added', () => {
        const productsCollection: IProducts[] = [sampleWithRequiredData];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, undefined, null);
        expect(expectedResult).toEqual(productsCollection);
      });
    });

    describe('compareProducts', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProducts(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProducts(entity1, entity2);
        const compareResult2 = service.compareProducts(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProducts(entity1, entity2);
        const compareResult2 = service.compareProducts(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProducts(entity1, entity2);
        const compareResult2 = service.compareProducts(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
