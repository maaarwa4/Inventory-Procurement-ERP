import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Supplier } from '../supplier/supplier.service'; // ✅ utiliser l'interface centrale

// DTO backend
export interface PurchaseOrderItemDTO {
  id?: number;
  purchaseOrderId?: number;
  product: { id: number, name?: string;  }; 
  quantity: number;
  unitPrice: number;
}


export interface SupplierDTO {
    id: number;
    firstName?: string; // optionnel pour TS
    lastName?: string;
    companyName?: string;
    email?: string;
    phone?: string;
    address?: string;
    country?: string;
    isActive?: boolean;
  }
  

export interface Product {
  id: number;
  name: string;
  price: number;
}

export interface PurchaseOrderDTO {
  id?: number;
  status: string;
  supplier: SupplierDTO; // ✅ type correct
  items: PurchaseOrderItemDTO[];
  creationDate?: string;
  totalAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class PurchaseOrderService {
  private apiUrl = 'http://localhost:8080/api/purchase-orders';
  private suppliersUrl = 'http://localhost:8080/api/suppliers';
  private productsUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  getAllOrders(): Observable<PurchaseOrderDTO[] | PurchaseOrderDTO> {
    return this.http.get<PurchaseOrderDTO[] | PurchaseOrderDTO>(this.apiUrl);
  }

  createOrder(order: PurchaseOrderDTO): Observable<PurchaseOrderDTO> {
    return this.http.post<PurchaseOrderDTO>(this.apiUrl, order);
  }

  updateOrder(id: number, order: PurchaseOrderDTO): Observable<PurchaseOrderDTO> {
    return this.http.put<PurchaseOrderDTO>(`${this.apiUrl}/${id}`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  downloadPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }

  downloadPdfFile(id: number, filename: string): void {
    this.downloadPdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Erreur téléchargement PDF', err)
    });
  }

  getAllSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(this.suppliersUrl);
  }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productsUrl);
  }
}
