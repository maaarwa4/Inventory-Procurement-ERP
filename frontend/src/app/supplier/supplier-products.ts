// src/app/components/supplier-products/supplier-products.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SupplierService } from './supplier.service';
import { Product } from '../models/product';
import { ProductCreateDTO } from '../models/product-create-dto';
import { DropdownData } from '../models/dropdown-data';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-supplier-products',
  templateUrl: './supplier-products.html',
  imports: [CommonModule, CurrencyPipe, FormsModule],
  standalone: true,
  styleUrls: ['./supplier-products.css']
})
export class SupplierProducts implements OnInit {
  products: Product[] = [];
  supplierId!: number;

  showPopup = false;
  serverError = '';
  imagePreviewUrl: string | null = null;

  brands: string[] = [];
  categories: string[] = [];
  colors: string[] = [];
  storageCapacities: string[] = [];

  form: any = this.emptyForm();

  constructor(
    private route: ActivatedRoute,
    private supplierService: SupplierService
  ) {}

  ngOnInit(): void {
    this.supplierId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.supplierId) this.loadProducts();
    this.loadDropdowns();
  }

  private emptyForm(): any {
    return {
      id: null,
      name: '',
      description: '',
      brand: '',
      category: '',
      price: null,
      stock_quantity: 0,
      color: '',
      storage: '',
      model: '',
      screen_size: '',
      network_type: '',
      image_url: ''
    };
  }

  loadProducts(): void {
    this.supplierService.getProductsBySupplier(this.supplierId).subscribe({
      next: (data) => this.products = data,
      error: (err) => console.error('[LOAD PRODUCTS]', err)
    });
  }

  loadDropdowns(): void {
    this.supplierService.getDropdownData().subscribe({
      next: (d: DropdownData) => {
        this.brands = d.brands ?? [];
        this.categories = d.categories ?? [];
        this.colors = d.colors ?? [];
        this.storageCapacities = d.storageOptions ?? [];
      },
      error: () => {
        this.brands = ['APPLE','SAMSUNG','HUAWEI','OPPO','XIAOMI','REALME','NOKIA','LG','SONY','MOTOROLA','ASUS','HONOR','GOOGLE','INFINIX','ONEPLUS'];
        this.categories = ['SMARTPHONE','TABLET','LAPTOP','SMARTWATCH','ACCESSORY'];
        this.colors = ['BLACK','WHITE','SILVER','GOLD','BLUE','RED','GREEN','PURPLE','YELLOW','GRAY'];
        this.storageCapacities = ['GB16','GB32','GB64','GB128','GB256','GB512','TB1'];
      }
    });
  }

  openPopup(): void {
    this.showPopup = true;
    this.serverError = '';
    this.form = this.emptyForm();
    this.imagePreviewUrl = null;
  }

  closePopup(): void {
    this.showPopup = false;
    this.serverError = '';
  }

  saveProduct(): void {
    if (!this.supplierId) {
      this.serverError = 'Fournisseur introuvable';
      return;
    }

    if (!this.form.name || !this.form.brand || !this.form.category || this.form.price == null || this.form.price <= 0) {
      this.serverError = 'Veuillez remplir Name, Brand, Category et Price (> 0).';
      return;
    }

    if (this.form.id) {
      // === UPDATE === - CORRECTION: Utiliser la même structure que CREATE
      const updateDto: ProductCreateDTO = {
        name: this.form.name.trim(),
        brand: String(this.form.brand).toUpperCase(),
        category: String(this.form.category).toUpperCase(),
        price: Number(this.form.price),
        stock_quantity: Number(this.form.stock_quantity) || 0,
        supplier_id: this.supplierId, // CORRECTION: Utiliser supplier_id au lieu de supplier: {id}
        description: this.form.description?.trim(),
        color: this.form.color ? String(this.form.color).toUpperCase() : undefined,
        storage: this.form.storage ? String(this.form.storage).toUpperCase() : undefined,
        model: this.form.model?.trim(),
        screen_size: this.form.screen_size?.trim(),
        network_type: this.form.network_type?.trim(),
        image_url: this.form.image_url?.trim()
      };

      console.log('🔵 UPDATE DTO envoyé:', updateDto);

      this.supplierService.updateProduct(this.form.id, updateDto).subscribe({
        next: (updated) => {
          console.log('✅ Produit mis à jour:', updated);
          const idx = this.products.findIndex(p => p.id === updated.id);
          if (idx > -1) this.products[idx] = updated;
          this.closePopup();
        },
        error: (err) => {
          console.error('[UPDATE] ERREUR:', err);
          console.error('Response:', err.error);
          this.serverError = err.error?.message || 'Impossible de modifier ce produit.';
        }
      });

    } else {
      // === CREATE === 
      const createDto: ProductCreateDTO = {
        name: this.form.name.trim(),
        brand: String(this.form.brand).toUpperCase(),
        category: String(this.form.category).toUpperCase(),
        price: Number(this.form.price),
        stock_quantity: Number(this.form.stock_quantity) || 0,
        supplier_id: this.supplierId,
        description: this.form.description?.trim(),
        color: this.form.color ? String(this.form.color).toUpperCase() : undefined,
        storage: this.form.storage ? String(this.form.storage).toUpperCase() : undefined,
        model: this.form.model?.trim(),
        screen_size: this.form.screen_size?.trim(),
        network_type: this.form.network_type?.trim(),
        image_url: this.form.image_url?.trim()
      };

      console.log('🟢 CREATE DTO envoyé:', createDto);

      this.supplierService.addProduct(createDto).subscribe({
        next: (saved) => {
          console.log('✅ Produit créé:', saved);
          this.products.push(saved);
          this.closePopup();
        },
        error: (err) => {
          console.error('[CREATE] ERREUR:', err);
          console.error('Response:', err.error);
          this.serverError = err.error?.message || 'Impossible d\'ajouter ce produit.';
        }
      });
    }
  }

  editProduct(product: Product): void {
    this.showPopup = true;
    this.serverError = '';
    
    // CORRECTION: Bien copier toutes les propriétés du produit
    this.form = {
      id: product.id,
      name: product.name || '',
      description: product.description || '',
      brand: product.brand || '',
      category: product.category || '',
      price: product.price || 0,
      stock_quantity: product.stock_quantity || 0,
      color: product.color || '',
      storage: product.storage || '',
      model: product.model || '',
      screen_size: product.screen_size || '',
      network_type: product.network_type || '',
      image_url: product.image_url || ''
    };
    
    console.log('📝 Form pour édition:', this.form);
  }

  deleteProduct(productId: number): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.supplierService.deleteProduct(productId).subscribe({
        next: () => {
          console.log('🗑️ Produit supprimé:', productId);
          this.products = this.products.filter(p => p.id !== productId);
        },
        error: (err) => {
          console.error('[DELETE] ERREUR', err);
          this.serverError = 'Impossible de supprimer ce produit.';
        }
      });
    }
  }
}