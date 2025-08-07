export interface Product {
  id?: number;
  name: string;
  description?: string;
  brand: string;         // Enum en Java → string ici
  category: string;
  price: number;
  stock_quantity: number;
  image_url?: string;
  model?: string;
  color?: string;
  storage?: string;
  screen_size?: string;
  network_type?: string;
  created_at?: string;
  updated_at?: string;
}
