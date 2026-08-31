/*
# Add categories to personal book records

1. Purpose
   Preserve useful subject/category metadata returned by Google Books or Open
   Library so it remains available after a book is saved to the personal library.

2. Modified Tables
   - `books`
     - `categories` (text array, nullable) — catalog-provided subjects/categories

3. Security
   - No policy changes. The new column is covered by the existing owner-scoped
     RLS policies.

4. Important Notes
   1. This is additive and does not alter or remove existing data.
   2. Manual entries may leave this field empty.
*/

ALTER TABLE books
  ADD COLUMN IF NOT EXISTS categories text[];