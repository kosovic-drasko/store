import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProducts } from '../products.model';

@Component({
  selector: 'jhi-products-detail',
  templateUrl: './products-detail.component.html',
})
export class ProductsDetailComponent implements OnInit {
  products: IProducts | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ products }) => {
      this.products = products;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
