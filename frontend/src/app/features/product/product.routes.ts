// Nom du fichier : src/app/features/product/product.routes.ts

import { Routes } from '@angular/router';
import { ProductListComponent } from './product-list/product-list';
import { ProductDetails } from './product-details/product-details';

export const PRODUCT_ROUTES: Routes = [
  { path: '', component: ProductListComponent },

  { path: ':id', component: ProductDetails }
];