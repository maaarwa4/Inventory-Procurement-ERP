import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';

// Angular Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';

import { ProductService } from '../../../core/services/product.service'; // ajuste le chemin si nécessaire
import { Product } from '../../../models/product'; // ajuste le chemin si nécessaire
import { MatCardModule } from '@angular/material/card'; // <-- pour <mat-card>
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; 
import { MatDialog } from '@angular/material/dialog';
import{ProductDetailDialog} from '../../../product-detail-dialog/product-detail-dialog'; 

// Interfaces
interface DropdownData {
  brands: string[];
  categories: string[];
  colors: string[];
  storageOptions: string[];
}

interface SearchCriteria {
  name: string;
  brand: string;
  category: string;
  priceMin: number | null;
  priceMax: number | null;
  storage: string;
  model: string;
}

interface SearchRequest {
  criteria: SearchCriteria;
  page: number;
  size: number;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}

@Component({
  selector: 'app-product-list',
  standalone: true,
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss'],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    MatPaginatorModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCardModule

  ]
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  isLoading = false;
  viewMode: 'grid' | 'list' = 'grid';

  searchCriteria: SearchCriteria = {
    name: '',
    brand: '',
    category: '',
    priceMin: null,
    priceMax: null,
    storage: '',
    model: ''
  };

  dropdownData: DropdownData = {
    brands: [],
    categories: [],
    colors: [],
    storageOptions: []
  };

  selectedPriceRange = '';
  selectedStorage = '';

  pagination = {
    length: 0,
    pageSize: 12,
    pageIndex: 0
  };

  constructor(private productService: ProductService,private dialog:MatDialog) {}

  ngOnInit(): void {
    this.loadDropdownData();
    this.searchProducts();
  }

  loadDropdownData(): void {
    this.productService.getDropdownData().subscribe({
      next: (data: DropdownData) => {
        this.dropdownData = data;
        console.log('Dropdown data loaded:', this.dropdownData);
      },
      error: (error) => {
        console.error('Failed to load dropdown data:', error);
      }
    });
  }

  searchProducts(): void {
    this.isLoading = true;

    const searchRequest: SearchRequest = {
      criteria: { ...this.searchCriteria },
      page: this.pagination.pageIndex,
      size: this.pagination.pageSize
    };

    console.log('Searching with criteria:', searchRequest);

    this.productService.searchProducts(searchRequest).subscribe({
      next: (response: PageResponse<Product>) => {
        this.products = response.content;
        this.pagination.length = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Search failed:', error);
        this.isLoading = false;
        this.products = [];
      }
    });
  }

  onPriceRangeChange(): void {
    if (this.selectedPriceRange === '') {
      this.searchCriteria.priceMin = null;
      this.searchCriteria.priceMax = null;
    } else if (this.selectedPriceRange === '1500+') {
      this.searchCriteria.priceMin = 1500;
      this.searchCriteria.priceMax = null;
    } else {
      const [min, max] = this.selectedPriceRange.split('-').map(Number);
      this.searchCriteria.priceMin = min;
      this.searchCriteria.priceMax = max;
    }
    this.resetPagination();
    this.searchProducts();
  }

  onStorageChange(): void {
    this.searchCriteria.storage = this.selectedStorage;
    this.resetPagination();
    this.searchProducts();
  }

  clearAllFilters(): void {
    this.searchCriteria = {
      name: '',
      brand: '',
      category: '',
      priceMin: null,
      priceMax: null,
      storage: '',
      model: ''
    };
    this.selectedPriceRange = '';
    this.selectedStorage = '';
    this.resetPagination();
    this.searchProducts();
  }

  private resetPagination(): void {
    this.pagination.pageIndex = 0;
  }

  onPageChange(event: PageEvent): void {
    this.pagination.pageIndex = event.pageIndex;
    this.pagination.pageSize = event.pageSize;
    this.searchProducts();
  }

  getProductSpecs(product: Product): string {
    const specs = [];
    if (product.storage) specs.push(this.formatStorageForDisplay(product.storage));
    if (product.color) specs.push(this.formatColorForDisplay(product.color));
    return specs.join(' • ');
  }

  formatColorForDisplay(color: string): string {
    const colorMap: { [key: string]: string } = {
      'BLACK': 'Black',
      'WHITE': 'White',
      'SILVER': 'Silver',
      'GOLD': 'Gold',
      'BLUE': 'Blue',
      'RED': 'Red',
      'GREEN': 'Green',
      'PURPLE': 'Purple',
      'YELLOW': 'Yellow',
      'GRAY': 'Gray'
    };
    return colorMap[color] || color.charAt(0).toUpperCase() + color.slice(1).toLowerCase();
  }

  formatStorageForDisplay(storage: string): string {
    const storageMap: { [key: string]: string } = {
      'GB16': '16 GB',
      'GB32': '32 GB',
      'GB64': '64 GB',
      'GB128': '128 GB',
      'GB256': '256 GB',
      'GB512': '512 GB',
      'TB1': '1 TB'
    };
    return storageMap[storage] || storage;
  }

  formatBrandForDisplay(brand: string): string {
    const brandMap: { [key: string]: string } = {
      'APPLE': 'Apple',
      'SAMSUNG': 'Samsung',
      'HUAWEI': 'Huawei',
      'OPPO': 'OPPO',
      'XIAOMI': 'Xiaomi',
      'REALME': 'Realme',
      'NOKIA': 'Nokia',
      'LG': 'LG',
      'SONY': 'Sony',
      'MOTOROLA': 'Motorola',
      'ASUS': 'ASUS',
      'HONOR': 'Honor',
      'GOOGLE': 'Google',
      'INFINIX': 'Infinix',
      'ONEPLUS': 'OnePlus'
    };
    return brandMap[brand] || brand.charAt(0).toUpperCase() + brand.slice(1).toLowerCase();
  }

  formatCategoryForDisplay(category: string): string {
    const categoryMap: { [key: string]: string } = {
      'SMARTPHONE': 'Smartphone',
      'TABLET': 'Tablet',
      'LAPTOP': 'Laptop',
      'SMARTWATCH': 'Smart Watch',
      'ACCESSORY': 'Accessory'
    };
    return categoryMap[category] || category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
  }

  onImageError(event: any, product: Product): void {
    event.target.src = 'assets/images/no-image.png';
  }
  openProductDetails(product: Product): void {
    this.dialog.open(ProductDetailDialog, {
      data: { product },
      width: '400px'
    });


}}
