// src/app/components/supplier-products/supplier-products.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SupplierService } from './supplier.service';
import { Product } from '../models/product';
import { DropdownData } from '../models/dropdown-data';
import { ProductCreateDTO } from '../models/product-create-dto';
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

  // Dropdowns
  brands: string[] = [];
  categories: string[] = [];
  colors: string[] = [];
  storageCapacities: string[] = [];

  // Formulaire local
  form: any = this.emptyForm();

  constructor(
    private route: ActivatedRoute,
    private supplierService: SupplierService
  ) {}

  ngOnInit(): void {
    this.supplierId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.supplierId) {
      this.loadProducts();
    }
    this.loadDropdowns();
  }

  private emptyForm(): any {
    return {
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
      next: (data) => (this.products = data),
      error: (err) => console.error('Erreur chargement produits', err)
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
      this.serverError = 'Veuillez remplir au minimum: Name, Brand, Category, Price (> 0).';
      return;
    }

    // ✅ Test avec DTO MINIMAL - seulement les champs obligatoires
    const dto: any = {
      name: this.form.name.trim(),
      brand: String(this.form.brand).toUpperCase(),
      category: String(this.form.category).toUpperCase(),
      price: parseFloat(this.form.price.toString()),
      stock_quantity: parseInt(this.form.stock_quantity) || 0,
      supplier_id: parseInt(this.supplierId.toString())
    };

    // TEMPORAIREMENT: commentons tous les champs optionnels
    /*
    if (this.form.description && this.form.description.trim()) {
      dto.description = this.form.description.trim();
    }

    if (this.form.color && this.form.color !== '') {
      dto.color = String(this.form.color).toUpperCase();
    }

    if (this.form.storage && this.form.storage !== '') {
      dto.storage = String(this.form.storage).toUpperCase();
    }

    if (this.form.model && this.form.model.trim()) {
      dto.model = this.form.model.trim();
    }

    if (this.form.screen_size && this.form.screen_size.trim()) {
      dto.screen_size = this.form.screen_size.trim();
    }

    if (this.form.network_type && this.form.network_type.trim()) {
      dto.network_type = this.form.network_type.trim();
    }

    if (this.form.image_url && this.form.image_url.trim()) {
      dto.image_url = this.form.image_url.trim();
    }
    */

    console.log('DTO envoyé:', JSON.stringify(dto, null, 2)); // JSON formaté pour débugger
    console.log('Supplier ID type:', typeof this.supplierId, this.supplierId);
    console.log('Form data:', this.form);

    this.supplierService.addProduct(dto).subscribe({
      next: (saved) => {
        console.log('Produit sauvé:', saved);
        this.products.push(saved);
        this.closePopup();
      },
      error: (err) => {
        console.error('Erreur ajout produit', err);
        console.error('Détails erreur:', err.error);
        console.error('Status:', err.status);
        console.error('Response complète:', err);
        
        let errorMessage = 'Impossible d\'ajouter le produit.';
        
        if (err.error) {
          if (typeof err.error === 'string') {
            errorMessage = `Erreur: ${err.error}`;
          } else if (err.error.message) {
            errorMessage = `Erreur: ${err.error.message}`;
          } else if (err.error.errors) {
            // Gestion des erreurs de validation - compatible ES5+
            const errorKeys = Object.keys(err.error.errors);
            const errors = errorKeys.map(key => err.error.errors[key]).join(', ');
            errorMessage = `Erreurs de validation: ${errors}`;
          }
        }
        
        this.serverError = errorMessage;
      }
    });
  }
}