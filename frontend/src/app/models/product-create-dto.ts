// src/app/models/product-create-dto.ts
export interface ProductCreateDTO {
  name: string;
  description?: string;
  brand: string;
  category: string;
  price: number;
  stock_quantity: number;
  color?: string;
  storage?: string;
  image_url?: string;
  model?: string;
  screen_size?: string;
  network_type?: string;
  supplier_id: number;
}