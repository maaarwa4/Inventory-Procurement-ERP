import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Supplier, SupplierService } from './supplier.service';

@Component({
  selector: 'app-supplier-add',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier-add.html',
  styleUrls: ['./supplier-add.scss']
})
export class SupplierAdd {
  supplier: Supplier = {
    id: undefined,
    firstName: '',
    lastName: '',
    companyName: '',
    phone: '',
    email: '',
    address: '',
    country: '',
    isActive: true
  };

  constructor(
    private supplierService: SupplierService,
    private router: Router
  ) {}

  saveSupplier(): void {
    console.log('Données envoyées au backend:', this.supplier); // 🔹 debug
    this.supplierService.create(this.supplier).subscribe({
      next: (res) => {
        console.log('Réponse backend:', res); // 🔹 debug
        alert('Supplier created successfully ✅');
        this.router.navigate(['/suppliers']);
      },
      error: (err) => {
        console.error('Erreur lors de la création:', err);
        alert('Erreur lors de la création du fournisseur ❌');
      }
    });
  }
  

  cancel(): void {
    this.router.navigate(['/suppliers']);
  }
}
