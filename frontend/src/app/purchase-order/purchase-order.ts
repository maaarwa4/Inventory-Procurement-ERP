import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { PurchaseOrderService, PurchaseOrderDTO, PurchaseOrderItemDTO } from './purchase-order.service';
import { SupplierService } from '../supplier/supplier.service';
import { ProductService } from '../core/services/product.service';
import { Product } from '../models/product';
import { Supplier } from '../supplier/supplier.service'; 
import { SupplierDTO } from './purchase-order.service'; 

import { CommonModule, NgForOf, NgIf } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-purchase-order',
  templateUrl: './purchase-order.html',
  styleUrls: ['./purchase-order.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NgForOf, NgIf]
})
export class PurchaseOrderComponent implements OnInit {

  orders: PurchaseOrderDTO[] = [];
  paginatedOrders: PurchaseOrderDTO[] = [];
  suppliers: Supplier[] = [];
  products: Product[] = [];

  createForm: FormGroup;
  editForm: FormGroup;

  // CORRECTION PRINCIPALE: Variables pour modales avec gestion Z-INDEX
  isCreateDialogOpen = false;
  isEditDialogOpen = false;
  selectedOrder: PurchaseOrderDTO | null = null;
  
  // État original de la page pour restauration après fermeture
  originalPageState = {
    scrollPosition: 0,
    bodyOverflow: ''
  };

  searchTerm = '';
  statusFilter = 'ALL';
  supplierFilter = 'ALL';
  statusOptions = [
    { value: 'PENDING', label: 'PENDING' },
    { value: 'APPROVED', label: 'APPROVED' },
    { value: 'DELIVERED', label: 'DELIVERED' }
  ];

  Math = Math;
  currentPage = 1;
  itemsPerPage = 10;
  totalItems = 0;
  totalPages = 0;
  paginationPages: number[] = [];

  successMessage = '';
  errorMessage = '';
  loading = false;

  stats = { total: 0, pending: 0, completed: 0, totalAmount: 0 };

  constructor(
    private fb: FormBuilder,
    private poService: PurchaseOrderService,
    private supplierService: SupplierService,
    private productService: ProductService
  ) {
    // FORMULAIRE CRÉATION: Tous les champs nécessaires
    this.createForm = this.fb.group({
      creationDate: [new Date().toISOString().split('T')[0], Validators.required],
      supplierId: ['', Validators.required],
      status: ['PENDING', Validators.required],
      items: this.fb.array([])
    });

    // FORMULAIRE MODIFICATION: Structure complète pour édition
    this.editForm = this.fb.group({
      id: [null],
      creationDate: ['', Validators.required],
      status: ['', Validators.required],
      supplierId: ['', Validators.required], // Permettre modification fournisseur
      items: this.fb.array([]) // Permettre modification items
    });
  }

  ngOnInit(): void {
    this.loadSuppliers();
    this.loadProducts(() => this.loadOrders());
    
    // CORRECTION: Écouter les événements clavier pour fermer modales
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape') {
        this.closeAllModals();
      }
    });
  }

  // Getters pour FormArrays
  get itemsFormArray(): FormArray {
    return this.createForm.get('items') as FormArray;
  }

  get editItemsFormArray(): FormArray {
    return this.editForm.get('items') as FormArray;
  }

  loadSuppliers(): void {
    this.supplierService.getAll().subscribe({
      next: suppliers => this.suppliers = suppliers,
      error: err => console.error(err)
    });
  }

  loadProducts(callback?: () => void): void {
    this.productService.getAll().subscribe({
      next: products => {
        this.products = products;
        if (callback) callback();
      },
      error: err => console.error(err)
    });
  }

  loadOrders(): void {
    this.loading = true;
    this.poService.getAllOrders().subscribe({
      next: (orders: PurchaseOrderDTO[] | PurchaseOrderDTO) => {
        this.orders = Array.isArray(orders) ? orders : [orders];
        this.applyFilters();
        this.computeStats();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.errorMessage = 'Erreur lors du chargement des commandes';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.orders];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(o =>
        (o.supplier?.firstName || '').toLowerCase().includes(term) ||
        (o.supplier?.lastName || '').toLowerCase().includes(term)
      );
    }

    if (this.statusFilter !== 'ALL') {
      filtered = filtered.filter(o => o.status === this.statusFilter);
    }

    if (this.supplierFilter !== 'ALL') {
      filtered = filtered.filter(o => o.supplier.id === +this.supplierFilter);
    }

    this.totalItems = filtered.length;
    this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
    this.paginationPages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    this.paginate(filtered);
  }

  paginate(filtered: PurchaseOrderDTO[]): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedOrders = filtered.slice(start, start + this.itemsPerPage);
  }

  onSearchChange(): void { this.currentPage = 1; this.applyFilters(); }
  onStatusFilterChange(): void { this.currentPage = 1; this.applyFilters(); }
  onSupplierFilterChange(): void { this.currentPage = 1; this.applyFilters(); }
  onPageChange(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.currentPage = page;
    this.applyFilters();
  }

  computeStats(): void {
    this.stats.total = this.orders.length;
    this.stats.pending = this.orders.filter(o => o.status === 'PENDING').length;
    this.stats.completed = this.orders.filter(o => o.status === 'DELIVERED').length;
    this.stats.totalAmount = this.orders.reduce((sum, o) => sum + this.calculateOrderTotal(o), 0);
  }

  calculateOrderTotal(order: PurchaseOrderDTO): number {
    return order.items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
  }

  getProductNames(order: PurchaseOrderDTO): string {
    if (!order.items || order.items.length === 0) return '';
    return order.items
      .map(item => {
        return item.product 
          ? `${item.product.name} (x${item.quantity})`
          : `Produit inconnu`;
      })
      .join(', ');
  }

  getTotalQuantity(order: PurchaseOrderDTO): number {
    return order.items.reduce((sum, i) => sum + i.quantity, 0);
  }

  formatCurrency(amount: number): string {
    return amount.toLocaleString('fr-MA', { style: 'currency', currency: 'MAD' });
  }

  formatDate(date?: string): string {
    return date ? new Date(date).toLocaleDateString('fr-FR') : '';
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING': return 'En Attente';
      case 'COMPLETED': return 'Terminée';
      case 'CANCELLED': return 'Annulée';
      default: return status;
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'pending';
      case 'COMPLETED': return 'completed';
      case 'CANCELLED': return 'cancelled';
      default: return '';
    }
  }

  getSupplierName(supplier: SupplierDTO): string {
    if (!supplier) return '';
    return `${supplier.firstName || ''} ${supplier.lastName || ''}`.trim();
  }

  // ============ CORRECTION PRINCIPALE: DIALOG CRÉATION ============

  /**
   * BUT: Ouvrir une modale dialog vraie au-dessus de la page
   * CHANGEMENTS: 
   * - Sauvegarde l'état de la page (scroll, overflow)
   * - Applique un z-index élevé et un fond d'overlay
   * - Bloque complètement l'interaction avec la page sous-jacente
   */
  openCreateDialog(): void {
    console.log('🔄 Ouverture dialog création...');
    
    // CORRECTION: Sauvegarder l'état actuel de la page
    this.savePageState();
    
    // Activation du dialog
    this.isCreateDialogOpen = true;
    
    // CORRECTION: Réinitialisation complète du formulaire
    this.createForm.reset({
      status: 'PENDING',
      creationDate: new Date().toISOString().split('T')[0],
      supplierId: ''
    });
    
    // Vider complètement le FormArray
    this.clearFormArray(this.itemsFormArray);
    
    this.clearMessages();
    
    // CORRECTION: Appliquer styles dialog (z-index + overlay)
    this.applyDialogStyles();
    
    console.log('✅ Dialog création ouvert');
  }

  /**
   * BUT: Fermer le dialog et restaurer l'état de la page
   */
  closeCreateDialog(): void {
    console.log('🔄 Fermeture dialog création...');
    
    this.isCreateDialogOpen = false;
    
    // CORRECTION: Restaurer l'état original de la page
    this.restorePageState();
    
    // Réinitialiser complètement
    this.createForm.reset();
    this.clearFormArray(this.itemsFormArray);
    
    console.log('✅ Dialog création fermé');
  }

  /**
   * BUT: Gérer la sélection/désélection des produits
   * CHANGEMENTS: Logique robuste pour éviter les doublons
   */
  toggleProductSelection(product: Product, event: any): void {
    const isChecked = event.target.checked;
    
    console.log(`🔄 ${isChecked ? 'Sélection' : 'Désélection'} produit: ${product.name}`);
    
    if (isChecked) {
      // Vérifier qu'il n'existe pas déjà
      const existingIndex = this.itemsFormArray.controls.findIndex(
        control => control.get('productId')?.value === product.id
      );
      
      if (existingIndex === -1) {
        const productFormGroup = this.fb.group({
          productId: [product.id, Validators.required],
          quantity: [1, [Validators.required, Validators.min(1)]],
          unitPrice: [product.price || 0, Validators.required]
        });
        this.itemsFormArray.push(productFormGroup);
      }
    } else {
      // Supprimer le produit
      const index = this.itemsFormArray.controls.findIndex(
        control => control.get('productId')?.value === product.id
      );
      if (index !== -1) {
        this.itemsFormArray.removeAt(index);
      }
    }
    
    console.log(`✅ Items actuels: ${this.itemsFormArray.length}`);
  }

  isProductSelected(product: Product): boolean {
    return this.itemsFormArray.controls.some(
      control => control.get('productId')?.value === product.id
    );
  }

  getProductFormGroup(product: Product): FormGroup | null {
    const control = this.itemsFormArray.controls.find(
      control => control.get('productId')?.value === product.id
    );
    return control as FormGroup || null;
  }

  /**
   * BUT: Créer la commande avec tous les détails
   * CHANGEMENTS: Validation renforcée et gestion d'erreurs
   */
  createOrder(): void {
    console.log('🔄 Création de commande...');
    
    if (this.createForm.invalid) {
      this.errorMessage = "Veuillez remplir tous les champs obligatoires.";
      this.clearMessages();
      return;
    }

    if (this.itemsFormArray.length === 0) {
      this.errorMessage = "Veuillez sélectionner au moins un produit.";
      this.clearMessages();
      return;
    }

    const formValue = this.createForm.value;
    console.log('📤 Données formulaire:', formValue);
    
    const items: PurchaseOrderItemDTO[] = this.itemsFormArray.controls.map(control => {
      const itemValue = control.value;
      return {
        product: { id: itemValue.productId },
        quantity: itemValue.quantity,
        unitPrice: itemValue.unitPrice
      };
    });

    const totalAmount = items.reduce(
      (sum, item) => sum + (item.quantity * item.unitPrice), 0
    );

    const order: PurchaseOrderDTO = {
      creationDate: formValue.creationDate,
      status: formValue.status,
      supplier: { id: formValue.supplierId },
      totalAmount: totalAmount,
      items: items
    };

    console.log("📤 Payload final:", order);

    this.poService.createOrder(order).subscribe({
      next: (createdOrder) => {
        console.log("✅ Commande créée:", createdOrder);
        this.successMessage = `Commande créée avec succès avec le statut "${this.getStatusLabel(createdOrder.status)}" !`;
        this.closeCreateDialog();
        this.refreshData();
        this.clearMessages();
      },
      error: (err) => {
        console.error("❌ Erreur création:", err);
        this.errorMessage = 'Erreur lors de la création de la commande.';
        this.clearMessages();
      }
    });
  }

  // ============ CORRECTION PRINCIPALE: DIALOG MODIFICATION COMPLÈTE ============

  /**
   * BUT: Ouvrir dialog modification avec TOUTES les données récupérées
   * CHANGEMENTS: 
   * - Récupération complète des données de la commande
   * - Chargement des items dans le FormArray
   * - Pré-sélection du fournisseur et des produits
   */
  openEditDialog(order: PurchaseOrderDTO): void {
    console.log('🔄 Ouverture dialog modification pour commande:', order.id);
    
    // Sauvegarder état page
    this.savePageState();
    
    // Cloner la commande sélectionnée
    this.selectedOrder = JSON.parse(JSON.stringify(order));
    
    // Activation du dialog
    this.isEditDialogOpen = true;
    
    // CORRECTION: Remplir TOUS les champs avec les données existantes
    this.editForm.patchValue({
      id: order.id,
      creationDate: order.creationDate,
      status: order.status,
      supplierId: order.supplier?.id || ''
    });
    
    // CORRECTION: Charger les items existants dans le FormArray
    this.loadExistingItemsInEditForm(order.items);
    
    this.applyDialogStyles();
    this.clearMessages();
    
    console.log('✅ Dialog modification ouvert avec données complètes');
  }

  /**
   * BUT: Charger les items existants dans le FormArray de modification
   * CHANGEMENTS: Logique robuste pour éviter les erreurs
   */
  private loadExistingItemsInEditForm(items: PurchaseOrderItemDTO[]): void {
    console.log('🔄 Chargement items existants:', items);
    
    // Vider le FormArray
    this.clearFormArray(this.editItemsFormArray);
    
    // Ajouter chaque item existant
    items.forEach(item => {
      if (item.product && item.product.id) {
        const itemFormGroup = this.fb.group({
          productId: [item.product.id, Validators.required],
          quantity: [item.quantity || 1, [Validators.required, Validators.min(1)]],
          unitPrice: [item.unitPrice || 0, Validators.required]
        });
        this.editItemsFormArray.push(itemFormGroup);
        console.log(`✅ Item ajouté: ${item.product.name} x${item.quantity}`);
      }
    });
    
    console.log(`✅ Total items chargés: ${this.editItemsFormArray.length}`);
  }

  /**
   * BUT: Fermer dialog modification et restaurer état
   */
  closeEditDialog(): void {
    console.log('🔄 Fermeture dialog modification...');
    
    this.isEditDialogOpen = false;
    this.selectedOrder = null;
    
    // Restaurer état page
    this.restorePageState();
    
    // Réinitialiser formulaire
    this.editForm.reset();
    this.clearFormArray(this.editItemsFormArray);
    
    console.log('✅ Dialog modification fermé');
  }

  /**
   * BUT: Gérer sélection produits dans modification
   * CHANGEMENTS: Logique pour modification (peut ajouter/supprimer)
   */
  toggleEditProductSelection(product: Product, event: any): void {
    const isChecked = event.target.checked;
    console.log(`🔄 Modification - ${isChecked ? 'Ajout' : 'Retrait'} produit: ${product.name}`);
    
    if (isChecked) {
      // Vérifier si déjà présent
      const existingIndex = this.editItemsFormArray.controls.findIndex(
        control => control.get('productId')?.value === product.id
      );
      
      if (existingIndex === -1) {
        const productFormGroup = this.fb.group({
          productId: [product.id, Validators.required],
          quantity: [1, [Validators.required, Validators.min(1)]],
          unitPrice: [product.price || 0, Validators.required]
        });
        this.editItemsFormArray.push(productFormGroup);
        console.log(`✅ Produit ajouté: ${product.name}`);
      }
    } else {
      // Supprimer le produit
      const index = this.editItemsFormArray.controls.findIndex(
        control => control.get('productId')?.value === product.id
      );
      if (index !== -1) {
        this.editItemsFormArray.removeAt(index);
        console.log(`✅ Produit supprimé: ${product.name}`);
      }
    }
  }

  isEditProductSelected(product: Product): boolean {
    return this.editItemsFormArray.controls.some(
      control => control.get('productId')?.value === product.id
    );
  }

  getEditProductFormGroup(product: Product): FormGroup | null {
    const control = this.editItemsFormArray.controls.find(
      control => control.get('productId')?.value === product.id
    );
    return control as FormGroup || null;
  }

  /**
   * BUT: Mettre à jour la commande avec toutes les modifications
   * CHANGEMENTS: Support modification complète (fournisseur + items + statut + date)
   */
  updateOrder(): void {
    console.log('🔄 Mise à jour commande...');
    
    if (this.editForm.invalid || !this.selectedOrder?.id) {
      this.errorMessage = "Données invalides pour la modification.";
      this.clearMessages();
      return;
    }

    if (this.editItemsFormArray.length === 0) {
      this.errorMessage = "La commande doit contenir au moins un produit.";
      this.clearMessages();
      return;
    }

    const formValue = this.editForm.value;
    console.log('📤 Nouvelles données:', formValue);

    // Construire les nouveaux items
    const newItems: PurchaseOrderItemDTO[] = this.editItemsFormArray.controls.map(control => {
      const itemValue = control.value;
      return {
        product: { id: itemValue.productId },
        quantity: itemValue.quantity,
        unitPrice: itemValue.unitPrice
      };
    });

    const newTotalAmount = newItems.reduce(
      (sum, item) => sum + (item.quantity * item.unitPrice), 0
    );

    // CORRECTION: Construire l'objet complet de modification
    const updatedOrder: PurchaseOrderDTO = {
      id: this.selectedOrder.id,
      creationDate: formValue.creationDate,
      status: formValue.status,
      supplier: { id: formValue.supplierId },
      items: newItems,
      totalAmount: newTotalAmount
    };

    console.log("📤 Payload modification complète:", updatedOrder);

    this.poService.updateOrder(this.selectedOrder.id, updatedOrder).subscribe({
      next: (updated) => {
        console.log("✅ Commande modifiée:", updated);
        this.successMessage = `Commande #${this.selectedOrder?.id} modifiée avec succès !`;
        this.closeEditDialog();
        this.refreshData();
        this.clearMessages();
      },
      error: (err) => {
        console.error("❌ Erreur modification:", err);
        this.errorMessage = 'Erreur lors de la modification de la commande.';
        this.clearMessages();
      }
    });
  }

  // ============ UTILITAIRES POUR DIALOG ============

  /**
   * BUT: Sauvegarder l'état actuel de la page avant dialog
   */
  private savePageState(): void {
    this.originalPageState.scrollPosition = window.pageYOffset || document.documentElement.scrollTop;
    this.originalPageState.bodyOverflow = document.body.style.overflow;
  }

  /**
   * BUT: Restaurer l'état original de la page après fermeture dialog
   */
  private restorePageState(): void {
    document.body.style.overflow = this.originalPageState.bodyOverflow;
    window.scrollTo(0, this.originalPageState.scrollPosition);
  }

  /**
   * BUT: Appliquer les styles CSS pour le dialog (z-index, overlay)
   */
  private applyDialogStyles(): void {
    document.body.style.overflow = 'hidden';
  }

  /**
   * BUT: Vider complètement un FormArray
   */
  private clearFormArray(formArray: FormArray): void {
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
  }

  /**
   * BUT: Fermer tous les dialogs ouverts
   */
  closeAllModals(): void {
    if (this.isCreateDialogOpen) {
      this.closeCreateDialog();
    }
    if (this.isEditDialogOpen) {
      this.closeEditDialog();
    }
  }

  /**
   * BUT: Fermer dialog si clic sur overlay (pas sur contenu)
   */
  onOverlayClick(event: Event, dialogType: 'create' | 'edit'): void {
    if (event.target === event.currentTarget) {
      if (dialogType === 'create') {
        this.closeCreateDialog();
      } else if (dialogType === 'edit') {
        this.closeEditDialog();
      }
    }
  }

  // ============ MÉTHODES EXISTANTES (inchangées) ============

  deleteOrder(order: PurchaseOrderDTO): void {
    if (!order.id) return;
    if (!confirm(`Voulez-vous vraiment supprimer la commande #${order.id} ?`)) return;

    this.poService.deleteOrder(order.id).subscribe({
      next: () => {
        this.successMessage = `Commande #${order.id} supprimée avec succès`;
        this.loadOrders();
        this.clearMessages();
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = `Erreur lors de la suppression de la commande #${order.id}`;
        this.clearMessages();
      }
    });
  }

  downloadPdf(orderId: number): void {
    this.poService.downloadPdfFile(orderId, `commande_${orderId}.pdf`);
  }

  refreshData(): void {
    this.loadOrders();
  }

  trackByOrderId(index: number, order: PurchaseOrderDTO): number {
    return order.id!;
  }

  private clearMessages(): void {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 5000);
  }
}