import { Component, Input, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IProducts, Products } from '../products.model';
import { ProductsService } from '../service/products.service';
import { FormBuilder } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-products-update',
  templateUrl: './products-update.component.html',
})
export class ProductsUpdateComponent implements OnInit {
  @Input() public id: any;
  @Input() public articalName: any;
  @Input() public articalPrice: any;
  isSaving = false;
  products: IProducts | null = null;

  //   editForm: ProductsFormGroup = this.productsFormService.createProductsFormGroup();
  //
  //   constructor(
  //     protected productsService: ProductsService,
  //     protected productsFormService: ProductsFormService,
  //     protected activatedRoute: ActivatedRoute
  //   ) {}
  //
  //   ngOnInit(): void {
  //     this.activatedRoute.data.subscribe(({ products }) => {
  //       this.products = products;
  //       if (products) {
  //         this.updateForm(products);
  //       }
  //     });
  //   }
  //
  //   previousState(): void {
  //     window.history.back();
  //   }
  //
  //   save(): void {
  //     this.isSaving = true;
  //     const products = this.productsFormService.getProducts(this.editForm);
  //     if (products.id !== null) {
  //       this.subscribeToSaveResponse(this.productsService.update(products));
  //     } else {
  //       this.subscribeToSaveResponse(this.productsService.create(products));
  //     }
  //   }
  //
  //   protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducts>>): void {
  //     result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
  //       // next: () => this.onSaveSuccess(),
  //       error: () => this.onSaveError(),
  //     });
  //   }
  //
  //   protected onSaveSuccess(): void {
  //     this.previousState();
  //   }
  //
  //   protected onSaveError(): void {
  //     // Api for inheritance.
  //   }
  //
  //   protected onSaveFinalize(): void {
  //     this.isSaving = false;
  //   }
  //
  //   protected updateForm(products: IProducts): void {
  //     this.products = products;
  //     this.productsFormService.resetForm(this.editForm, products);
  //   }
  // }
  editForm = this.fb.group({
    id: [],
    articalName: [],
    articalPrice: [],
  });

  constructor(
    protected productService: ProductsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected activeModal: NgbActiveModal
  ) {}

  ngOnInit(): void {
    this.updateForm();
  }

  previousState(): void {
    this.activeModal.close();
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.id !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducts>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.activeModal.close();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(): void {
    this.editForm.patchValue({
      id: this.id,
      articalName: this.articalName,
      articalPrice: this.articalPrice,
    });
  }

  protected createFromForm(): IProducts {
    return {
      ...new Products(this.id, this.articalName, this.articalPrice),
      id: this.editForm.get(['id'])!.value,
      articalName: this.editForm.get(['articalName'])!.value,
      articalPrice: this.editForm.get(['articalPrice'])!.value,
    };
  }
}
