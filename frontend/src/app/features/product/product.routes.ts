// Nom du fichier : src/app/features/product/product.routes.ts

import { Routes } from '@angular/router';
import { ProductListComponent } from './product-list/product-list';
import { ProductFormComponent } from './product-form/product-form';
import { ProductDetails } from './product-details/product-details';

export const PRODUCT_ROUTES: Routes = [
  { path: '', component: ProductListComponent },
  { path: 'new', component: ProductFormComponent },
  { path: 'edit/:id', component: ProductFormComponent },
  { path: ':id', component: ProductDetails }
];