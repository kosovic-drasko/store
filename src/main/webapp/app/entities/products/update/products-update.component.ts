import { Component, Input, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ProductsFormService, ProductsFormGroup } from './products-form.service';
import { IProducts } from '../products.model';
import { ProductsService } from '../service/products.service';

@Component({
  selector: 'jhi-products-update',
  templateUrl: './products-update.component.html',
})
export class ProductsUpdateComponent implements OnInit {
  @Input() public id: any;
  @Input() public articalName: any;
  @Input() public articakPrice: any;
  isSaving = false;
  products: IProducts | null = null;

  editForm: ProductsFormGroup = this.productsFormService.createProductsFormGroup();

  constructor(
    protected productsService: ProductsService,
    protected productsFormService: ProductsFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ products }) => {
      this.products = products;
      if (products) {
        this.updateForm(products);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const products = this.productsFormService.getProducts(this.editForm);
    if (products.id !== null) {
      this.subscribeToSaveResponse(this.productsService.update(products));
    } else {
      this.subscribeToSaveResponse(this.productsService.create(products));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducts>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      // next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(products: IProducts): void {
    this.products = products;
    this.productsFormService.resetForm(this.editForm, products);
  }
}
