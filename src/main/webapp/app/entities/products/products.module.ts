import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductsComponent } from './list/products.component';
import { ProductsDetailComponent } from './detail/products-detail.component';
import { ProductsUpdateComponent } from './update/products-update.component';
import { ProductsDeleteDialogComponent } from './delete/products-delete-dialog.component';
import { ProductsRoutingModule } from './route/products-routing.module';

@NgModule({
  imports: [SharedModule, ProductsRoutingModule],
  declarations: [ProductsComponent, ProductsDetailComponent, ProductsUpdateComponent, ProductsDeleteDialogComponent],
})
export class ProductsModule {}
