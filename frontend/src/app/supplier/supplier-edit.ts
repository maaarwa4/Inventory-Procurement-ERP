// src/app/features/supplier/supplier-edit.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Supplier, SupplierService } from './supplier.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-supplier-edit',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './supplier-edit.html',
  styleUrls: ['./supplier-edit.scss']
})
export class SupplierEdit implements OnInit {
  supplier: Supplier = { firstName: '', address: '' };

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private supplierService: SupplierService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.supplierService.getById(id).subscribe(data => this.supplier = data);
  }

  save(): void {
    this.supplierService.update(this.supplier.id!, this.supplier).subscribe(() => {
      alert('Fournisseur mis à jour !');
      this.router.navigate(['/suppliers']);
    });
  }
}
