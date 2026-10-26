-- Played at every startup, after Hibernate has created or updated the tables.
-- Each statement only inserts what is missing, so a restart never duplicates rows.
-- TRIM: HSQLDB types the strings of a multi-row VALUES as CHAR of the longest one,
-- so 'E_BOOK' would be stored as 'E_BOOK   ' and no longer match BookFormat.E_BOOK.

-- Every ProductType needs its price here: a missing one makes the catalog page fail.
-- JpaPriceRepositoryTest checks it against ProductType.entries.
MERGE INTO product_prices p
USING (VALUES ('HARDCOVER', 1000), ('POCKET', 750), ('E_BOOK', 500)) AS v (product_type, amount_in_cents)
ON p.product_type = TRIM(v.product_type)
WHEN NOT MATCHED THEN INSERT (product_type, amount_in_cents) VALUES (TRIM(v.product_type), v.amount_in_cents);

-- Starter books, only into an empty catalog.
INSERT INTO books (title, kind, format, price_in_cents)
SELECT TRIM(v.title), TRIM(v.kind), TRIM(v.format), v.price_in_cents
FROM (VALUES
    ('LSDA : La communauté de l''anneau', 'FANTASY', 'HARDCOVER', 1500),
    ('LSDA : Les deux tours', 'FANTASY', 'E_BOOK', 700),
    ('LSDA : Le retour du roi', 'FANTASY', 'POCKET', 1000)
) AS v (title, kind, format, price_in_cents)
WHERE NOT EXISTS (SELECT 1 FROM books);
