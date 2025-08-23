export interface Product {
  id: number;
  name: string;
  brand: string; // Enum Brand
  category: string; // Enum Category
  price: number;
  stock_quantity: number;
  image_url?: string;
  description?: string;
  network_type?: string; // Enum NetworkType
  screen_size?: string; // Enum ScreenSize
  storage?: string; // Enum StorageCapacity
  color?: string; // Enum Color
  model?: string;
  created_at?: string;
  updated_at?: string;
  supplierId?: number;
}