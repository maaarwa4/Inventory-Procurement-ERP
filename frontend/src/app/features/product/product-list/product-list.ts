// Nom du fichier : src/app/features/product/product-list/product-list.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

// Imports d'Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';

import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../models/product';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    HttpClientModule,

    // Modules Material
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatPaginatorModule
  ],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.css']
})
export class ProductListComponent implements OnInit {

  products: Product[] = [];
  page = 0;
  size = 12; // On peut afficher plus de cartes par page
  totalPages = 0;
  totalElements = 0; // Important pour le paginateur Material
  search = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getProducts(this.page, this.size, this.search)
      .subscribe(response => {
        this.products = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements; // Assurez-vous que votre API renvoie bien ce champ
      });
  }

  searchProducts(): void {
    this.page = 0;
    this.loadProducts();
  }
  
  // Gère les événements de changement de page du paginateur
  handlePageEvent(event: PageEvent) {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    this.loadProducts();
  }

  delete(id: number | undefined): void {
    if (id === undefined) return;
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.deleteProduct(id).subscribe(() => this.loadProducts());
    }
  }
}