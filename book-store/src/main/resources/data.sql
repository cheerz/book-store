MERGE INTO product_prices p
USING (VALUES ('HARDCOVER', 1000), ('POCKET', 750), ('E_BOOK', 500)) AS v (product_type, amount_in_cents)
ON p.product_type = TRIM(v.product_type)
WHEN NOT MATCHED THEN INSERT (product_type, amount_in_cents) VALUES (TRIM(v.product_type), v.amount_in_cents);

INSERT INTO books (title, kind, format, price_in_cents)
SELECT TRIM(v.title), TRIM(v.kind), TRIM(v.format), v.price_in_cents
FROM (VALUES
    ('LSDA : La communauté de l''anneau', 'FANTASY', 'HARDCOVER', 1500),
    ('LSDA : Les deux tours', 'FANTASY', 'E_BOOK', 700),
    ('LSDA : Le retour du roi', 'FANTASY', 'POCKET', 1000)
) AS v (title, kind, format, price_in_cents)
WHERE NOT EXISTS (SELECT 1 FROM books);
