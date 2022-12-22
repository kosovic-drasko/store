import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProducts } from '../products.model';

export type PartialUpdateProducts = Partial<IProducts> & Pick<IProducts, 'id'>;

export type EntityResponseType = HttpResponse<IProducts>;
export type EntityArrayResponseType = HttpResponse<IProducts[]>;

@Injectable({ providedIn: 'root' })
export class ProductsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(products: IProducts): Observable<EntityResponseType> {
    return this.http.post<IProducts>(this.resourceUrl, products, { observe: 'response' });
  }

  update(products: IProducts): Observable<EntityResponseType> {
    return this.http.put<IProducts>(`${this.resourceUrl}/${this.getProductsIdentifier(products)}`, products, { observe: 'response' });
  }

  partialUpdate(products: PartialUpdateProducts): Observable<EntityResponseType> {
    return this.http.patch<IProducts>(`${this.resourceUrl}/${this.getProductsIdentifier(products)}`, products, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProducts>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProducts[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProductsIdentifier(products: Pick<IProducts, 'id'>): number {
    return products.id;
  }

  compareProducts(o1: Pick<IProducts, 'id'> | null, o2: Pick<IProducts, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductsIdentifier(o1) === this.getProductsIdentifier(o2) : o1 === o2;
  }

  addProductsToCollectionIfMissing<Type extends Pick<IProducts, 'id'>>(
    productsCollection: Type[],
    ...productsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const products: Type[] = productsToCheck.filter(isPresent);
    if (products.length > 0) {
      const productsCollectionIdentifiers = productsCollection.map(productsItem => this.getProductsIdentifier(productsItem)!);
      const productsToAdd = products.filter(productsItem => {
        const productsIdentifier = this.getProductsIdentifier(productsItem);
        if (productsCollectionIdentifiers.includes(productsIdentifier)) {
          return false;
        }
        productsCollectionIdentifiers.push(productsIdentifier);
        return true;
      });
      return [...productsToAdd, ...productsCollection];
    }
    return productsCollection;
  }
}
