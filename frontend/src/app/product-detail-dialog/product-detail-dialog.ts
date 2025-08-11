import { Component, Inject } from '@angular/core';
import { MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { Product } from '../models/product'; 

@Component({
  selector: 'app-product-detail-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './product-detail-dialog.html',
})
export class ProductDetailDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public product: Product) {}
}


