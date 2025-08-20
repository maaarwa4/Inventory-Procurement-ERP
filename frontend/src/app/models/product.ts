export interface Product {
  id: number;
  name: string;
  brand: string; // Enum Brand
  category: string; // Enum Category
  price: number;
  stock_quantity: number;
  image_url?: string;
  description?: string;
  storage?: string; // Enum StorageCapacity
  color?: string; // Enum Color
  model?: string;
  created_at?: string;
  updated_at?: string;
  supplierId?: number;
}