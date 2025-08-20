// src/app/services/supplier.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product';
import { ProductCreateDTO } from '../models/product-create-dto';
import { DropdownData } from '../models/dropdown-data';

export interface Supplier {
  id?: number;
  firstName: string;
  lastName?: string;
  companyName?: string;
  phone?: string;
  address: string;
  email?: string;
  country?: string;
  isActive?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  private apiUrl = 'http://localhost:8080/api/suppliers';
  private productsUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(this.apiUrl);
  }

  getById(id: number): Observable<Supplier> {
    return this.http.get<Supplier>(`${this.apiUrl}/${id}`);
  }

  create(supplier: Supplier): Observable<Supplier> {
    return this.http.post<Supplier>(this.apiUrl, supplier);
  }

  update(id: number, supplier: Supplier): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.apiUrl}/${id}`, supplier);
  }

  deactivate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // === PRODUCTS ===
  getProductsBySupplier(supplierId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.productsUrl}/supplier/${supplierId}`);
  }

  getDropdownData(): Observable<DropdownData> {
    return this.http.get<DropdownData>(`${this.productsUrl}/dropdown-data`);
  }

  addProduct(dto: ProductCreateDTO): Observable<Product> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });

    console.log('Service - URL:', `${this.productsUrl}`);
    console.log('Service - DTO envoyé:', JSON.stringify(dto, null, 2));
    console.log('Service - Headers:', headers);

    return this.http.post<Product>(`${this.productsUrl}`, dto, { headers });
  }
}