import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Supplier, SupplierService } from './supplier.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [CommonModule , RouterModule, FormsModule],
  templateUrl: './supplier-list.html',
  styleUrls: ['./supplier-list.scss']
})

export class SupplierList implements OnInit {
  suppliers: Supplier[] = [];           
  allSuppliers: Supplier[] = [];        
  searchKeyword: string = '';             
  statusFilter: 'all' | 'active' | 'inactive' = 'all'; 

  constructor(
    private supplierService: SupplierService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadSuppliers();
  }

  // Charger la liste depuis le service
  loadSuppliers(): void {
    this.supplierService.getAll().subscribe(data => {
      this.allSuppliers = data;
      this.applyFilters();
    });
  }

  // Appliquer recherche + filtre
  applyFilters(): void {
    let filtered = this.allSuppliers;

    // Filtrer par mot-clé (nom d'entreprise)
    if (this.searchKeyword.trim()) {
      const keyword = this.searchKeyword.toLowerCase();
      filtered = filtered.filter(s =>
        s.companyName?.toLowerCase().includes(keyword)
      );
    }

    // Filtrer par statut
    if (this.statusFilter === 'active') {
      filtered = filtered.filter(s => s.isActive);
    } else if (this.statusFilter === 'inactive') {
      filtered = filtered.filter(s => !s.isActive);
    }

    this.suppliers = filtered;
  }

  // Appelé quand l'utilisateur tape dans la recherche
  onSearchChange(): void {
    this.applyFilters();
  }

  // Appelé quand l'utilisateur change le filtre actif/inactif
  onStatusChange(status: 'all' | 'active' | 'inactive'): void {
    this.statusFilter = status;
    this.applyFilters();
  }

  // Actions existantes
  editSupplier(id: number): void {
    this.router.navigate(['/suppliers/edit', id]);
  }
  addSupplier(): void {
    this.router.navigate(['/suppliers/add']); 
  }
  

  viewProducts(id: number): void {
    this.router.navigate(['/suppliers', id, 'products']);
  }

  deactivateSupplier(supplier: Supplier): void {
    const action = supplier.isActive ? 'désactiver' : 'activer';
    if (confirm(`Voulez-vous vraiment ${action} ce fournisseur ?`)) {
      if (supplier.isActive) {
        // 🔴 Désactiver
        this.supplierService.deactivate(supplier.id!).subscribe({
          next: () => this.loadSuppliers(),
          error: (err) => console.error('Erreur désactivation fournisseur', err)
        });
      } else {
        // 🟢 Activer
        this.supplierService.activate(supplier.id!).subscribe({
          next: () => this.loadSuppliers(),
          error: (err) => console.error('Erreur activation fournisseur', err)
        });
      }
    }
  }
  


  deleteSupplier(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer définitivement ce fournisseur ?')) {
      this.supplierService. deleteSupplier(id).subscribe({
        next: () => this.loadSuppliers(),
        error: (err) => console.error('Erreur suppression fournisseur', err)
      });
    }
  }
  
}
