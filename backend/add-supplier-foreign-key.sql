-- Migration pour ajouter la relation supplier-product
-- Exécute ces commandes dans PostgreSQL

-- 1. Ajouter la colonne supplier_id
ALTER TABLE product ADD COLUMN supplier_id BIGINT NULL;

-- 2. Créer la contrainte de foreign key
ALTER TABLE product
  ADD CONSTRAINT fk_product_supplier
  FOREIGN KEY (supplier_id) REFERENCES supplier(id)
  ON DELETE SET NULL;

-- 3. Créer un index pour optimiser les requêtes
CREATE INDEX idx_product_supplier ON product(supplier_id);

-- 4. Vérifier que la migration a fonctionné
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'product' AND column_name = 'supplier_id'; 