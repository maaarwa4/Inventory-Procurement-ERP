import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'products',
    pathMatch: 'full'
  },
  {
    path: 'products',
    loadChildren: () =>
      import('./features/product/product.routes').then(m => m.PRODUCT_ROUTES)
  },
  {
    path: 'suppliers',
    loadChildren: () =>
      import('./supplier/supplier.routes').then(m => m.SUPPLIER_ROUTES)
  },
  {
    path: 'purchases',
    loadChildren: () =>
      import('./purchase-order/purchase-order.routes').then(m => m.PURCHASE_ORDER_ROUTES)
  }
];
