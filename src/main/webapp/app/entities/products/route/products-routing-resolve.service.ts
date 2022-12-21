import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProducts } from '../products.model';
import { ProductsService } from '../service/products.service';

@Injectable({ providedIn: 'root' })
export class ProductsRoutingResolveService implements Resolve<IProducts | null> {
  constructor(protected service: ProductsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProducts | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((products: HttpResponse<IProducts>) => {
          if (products.body) {
            return of(products.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
