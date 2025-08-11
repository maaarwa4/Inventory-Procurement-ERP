import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { HttpErrorResponse } from '@angular/common/http';

import { ProductService } from '../../../core/services/product.service';
import { Product, ProductAlias } from '../../../models/product';
import { MatDialog } from '@angular/material/dialog';
import { ProductDetailDialog} from '../../../product-detail-dialog/product-detail-dialog';
import { ProductSearchCriteria } from '../../../models/product-search-criteria';
import { PageResponse } from '../../../models/page-response';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonToggleModule
  ],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductListComponent implements OnInit {
  products: ProductAlias[] = [];
  isLoading = true;

  searchCriteria: ProductSearchCriteria = {
    name: '',
    brand: '',
    category: '',
    priceMin: null,
    priceMax: null,
    storage: null
  };

  selectedPriceRange: string = '';
  selectedStorage: string = '';
  sortBy: string = 'name';
  viewMode: 'grid' | 'list' = 'grid';

  dropdownData = {
    brands: [] as string[],
    categories: [] as string[],
    storageOptions: [] as string[]
  };

  pagination = {
    pageIndex: 0,
    pageSize: 12,
    length: 0
  };

  constructor(
    private productService: ProductService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    console.log('Component initialized');
    this.loadDropdownData();
    this.loadAllProducts();
  }

  loadAllProducts(): void {
    this.isLoading = true;
    console.log('Loading all products...');
    
    this.productService.getProducts(this.pagination.pageIndex, this.pagination.pageSize).subscribe({
      next: (response: any) => { // Type temporaire pour debug
        console.log('Raw response from backend:', response);
        
        // Vérifier si c'est un PageResponse ou directement un array
        let products: any[] = [];
        if (Array.isArray(response)) {
          products = response;
          this.pagination.length = response.length;
        } else if (response.content) {
          products = response.content;
          this.pagination.length = response.totalElements || products.length;
        }
        
        console.log('Products to map:', products);
        this.products = this.mapProducts(products);
        console.log('Mapped products:', this.products);
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Failed to load products:', err);
        this.products = [];
        this.pagination.length = 0;
        this.isLoading = false;
      }
    });
  }

  loadDropdownData(): void {
    this.productService.getDropdownData().subscribe({
      next: (data: any) => {
        console.log('Dropdown data loaded:', data);
        this.dropdownData.brands = data.brands || [];
        this.dropdownData.categories = data.categories || [];
        this.dropdownData.storageOptions = data.storageOptions || [];
      },
      error: (err: HttpErrorResponse) => {
        console.error('Dropdown data loading failed', err);
      }
    });
  }

  searchProducts(): void {
    this.isLoading = true;
    console.log('Searching with criteria:', this.searchCriteria);
  
    const criteria: ProductSearchCriteria = {
      name: this.searchCriteria.name || null,
      brand: this.searchCriteria.brand || null,
      category: this.searchCriteria.category || null,
      priceMin: null,
      priceMax: null,
      storage: this.selectedStorage || null
    };
  
    // Gérer la fourchette de prix
    if (this.selectedPriceRange) {
      if (this.selectedPriceRange.includes('+')) {
        criteria.priceMin = +this.selectedPriceRange.replace('+', '');
        criteria.priceMax = null;
      } else {
        const [minStr, maxStr] = this.selectedPriceRange.split('-');
        criteria.priceMin = Number(minStr);
        criteria.priceMax = Number(maxStr);
      }
    }
  
    // Vérifier si des filtres sont appliqués
    const hasFilters = criteria.name || criteria.brand || criteria.category || 
                      criteria.priceMin !== null || criteria.priceMax !== null || criteria.storage;
    
    if (!hasFilters) {
      this.loadAllProducts();
      return;
    }

    this.productService.searchProducts(criteria, this.pagination.pageIndex, this.pagination.pageSize).subscribe({
      next: (response: PageResponse<Product>) => {
        console.log('Search results:', response);
        this.products = this.mapProducts(response.content);
        this.pagination.length = response.totalElements;
        this.isLoading = false;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Search failed:', err);
        this.products = [];
        this.pagination.length = 0;
        this.isLoading = false;
      }
    });
  }
  
  clearAllFilters(): void {
    this.searchCriteria = {
      name: '',
      brand: '',
      category: '',
      priceMin: null,
      priceMax: null,
      storage: null
    };
    this.selectedPriceRange = '';
    this.selectedStorage = '';
    this.pagination.pageIndex = 0;
    this.loadAllProducts();
  }

  onSortChange(): void {
    switch (this.sortBy) {
      case 'name':
        this.products.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'name-desc':
        this.products.sort((a, b) => b.name.localeCompare(a.name));
        break;
      case 'price-asc':
        this.products.sort((a, b) => a.price - b.price);
        break;
      case 'price-desc':
        this.products.sort((a, b) => b.price - a.price);
        break;
      case 'brand':
        this.products.sort((a, b) => a.brand.localeCompare(b.brand));
        break;
    }
  }

  onPriceRangeChange(): void {
    this.searchProducts();
  }

  onStorageChange(): void {
    this.searchProducts();
  }

  onPageChange(event: PageEvent): void {
    this.pagination.pageIndex = event.pageIndex;
    this.pagination.pageSize = event.pageSize;
    
    const hasFilters = this.searchCriteria.name || this.searchCriteria.brand || 
                      this.searchCriteria.category || this.selectedPriceRange || this.selectedStorage;
    
    if (hasFilters) {
      this.searchProducts();
    } else {
      this.loadAllProducts();
    }
  }

  viewDetails(productId: number): void {
    this.router.navigate(['/products', productId]);
  }

  getProductSpecs(product: ProductAlias): string {
    const specs = [];
    if (product.model) specs.push(`Model: ${product.model}`);
    if (product.color) specs.push(`Color: ${product.color}`);
    if (product.storage) specs.push(`Storage: ${product.storage}`);
    if (product.screen_size) specs.push(`Screen: ${product.screen_size}"`);
    return specs.join(' • ');
  }

  onImageError(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    imgElement.src = 'assets/default-product.jpg';
  }

  // MAPPING CORRIGÉ : Conversion des noms de champs backend → frontend
  private mapProducts(products: any[]): ProductAlias[] {
    return products.map((p: any) => {
      return {
        id: p.id,
        name: p.name || '',
        description: p.description || '',
        brand: p.brand || '',
        category: p.category || '',
        price: p.price || 0,
        // Conversion : backend utilise snake_case, frontend aussi
        stock_quantity: p.stock_quantity || p.stockQuantity || 0,
        image_url: p.image_url || p.imageUrl || '',
        model: p.model || '',
        color: p.color || '',
        storage: p.storage || '',
        screen_size: p.screen_size || p.screenSize || '',
        network_type: p.network_type || p.networkType || '',
        created_at: p.created_at || p.createdAt || '',
        updated_at: p.updated_at || p.updatedAt || ''
      } as ProductAlias;
    });
  }

  openProductDetails(product: ProductAlias): void {
    this.dialog.open(ProductDetailDialog, {
      data: product,
      width: '600px'
    });
  }
}