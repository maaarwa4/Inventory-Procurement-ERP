// src/app/features/supplier/supplier.routes.ts
import { Routes } from '@angular/router';
import { SupplierList } from './supplier-list';
import { SupplierProducts } from './supplier-products';
import { SupplierEdit } from './supplier-edit';
import { SupplierAdd } from './supplier-add';

export const SUPPLIER_ROUTES: Routes = [
  { path: '', component: SupplierList },
  {path: 'edit/:id', component: SupplierEdit },
  { path: ':id/products', component: SupplierProducts },
  {path: 'add', component: SupplierAdd }
];
