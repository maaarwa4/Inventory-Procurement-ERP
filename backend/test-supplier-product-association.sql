-- Script pour tester l'association supplier-product
-- Exécute ce script après avoir ajouté la foreign key

-- 1. Insérer quelques suppliers de test
INSERT INTO supplier (first_name, last_name, company_name, phone, city, email, country, is_active) VALUES
('Ahmed', 'Alaoui', 'TechMaroc SARL', '+212612345678', 'Casablanca', 'ahmed@techmaroc.ma', 'Maroc', true),
('Fatima', 'Benjelloun', 'Digital Solutions', '+212698765432', 'Rabat', 'fatima@digitalsolutions.ma', 'Maroc', true),
('Karim', 'Tazi', 'Electronics Plus', '+212654321098', 'Marrakech', 'karim@electronicsplus.ma', 'Maroc', true),
('Amina', 'El Fassi', 'Smart Devices', '+212623456789', 'Fès', 'amina@smartdevices.ma', 'Maroc', true);

-- 2. Associer des produits existants à des suppliers
UPDATE product SET supplier_id = 1 WHERE id IN (1, 2); -- iPhone et Galaxy à TechMaroc
UPDATE product SET supplier_id = 2 WHERE id IN (3, 4); -- Huawei et MacBook à Digital Solutions  
UPDATE product SET supplier_id = 3 WHERE id = 5; -- Xiaomi à Electronics Plus

-- 3. Vérifier les associations
SELECT 
    p.id as product_id,
    p.name as product_name,
    s.id as supplier_id,
    s.company_name as supplier_name,
    s.city as supplier_city
FROM product p
LEFT JOIN supplier s ON p.supplier_id = s.id
ORDER BY p.id;

-- 4. Compter les produits par supplier
SELECT 
    s.company_name,
    COUNT(p.id) as product_count
FROM supplier s
LEFT JOIN product p ON s.id = p.supplier_id
GROUP BY s.id, s.company_name
ORDER BY product_count DESC; 