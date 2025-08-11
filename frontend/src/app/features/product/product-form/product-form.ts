// Nom du fichier : src/app/features/product/product-form/product-form.component.ts
// Ce code est déjà correct d'après ce que vous avez fourni. Aucune modification n'est nécessaire.

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../models/product'
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-product-form',
  encapsulation: ViewEncapsulation.None,
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-form.html',
  styleUrls: ['./product-form.css']
})
export class ProductFormComponent implements OnInit {
  product: Product = { name: '', description: '', price: 0, stockQuantity: 0, brand: '', category: '' };
  isEdit = false;
  id!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    const paramId = this.route.snapshot.params['id'];
    if (paramId) {
      this.id = +paramId;
      this.isEdit = true;
      this.productService.getProductById(this.id).subscribe(data => this.product = data);
    }
  }

  save() {
    if (this.isEdit) {
      this.productService.updateProduct(this.id, this.product).subscribe(() => this.router.navigate(['/products']));
    } else {
      this.productService.createProduct(this.product).subscribe(() => this.router.navigate(['/products']));
    }
  }
}