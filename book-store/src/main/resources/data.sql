MERGE INTO product_formats f
USING (VALUES ('HARDCOVER', 'hardcover', 1000), ('POCKET', 'pocket', 750), ('E_BOOK', 'e-book', 500)) AS v (code, label, surcharge_in_cents)
ON f.code = TRIM(v.code)
WHEN NOT MATCHED THEN INSERT (code, label, surcharge_in_cents) VALUES (TRIM(v.code), TRIM(v.label), v.surcharge_in_cents);

INSERT INTO books (title, kind, format, price_in_cents)
SELECT TRIM(v.title), TRIM(v.kind), TRIM(v.format), v.price_in_cents
FROM (VALUES
    ('LSDA : La communauté de l''anneau', 'FANTASY', 'HARDCOVER', 1500),
    ('LSDA : Les deux tours', 'FANTASY', 'E_BOOK', 700),
    ('LSDA : Le retour du roi', 'FANTASY', 'POCKET', 1000)
) AS v (title, kind, format, price_in_cents)
WHERE NOT EXISTS (SELECT 1 FROM books);
