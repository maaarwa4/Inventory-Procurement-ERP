import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Product } from '../models/product'; 

@Component({
  selector: 'app-product-detail-dialog',
  standalone: true,
  templateUrl: './product-detail-dialog.html',
  styleUrls: ['./product-detail-dialog.scss'],
  imports: [CommonModule, MatDialogModule, MatButtonModule]
})
export class ProductDetailDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { product: Product }) {}

  onImageError(event: any) {
    event.target.src = 'assets/images/no-image.png';
  }
}
