/*
# Add catalog source columns to books table

1. Purpose
   Tracks which external catalog a book came from (Google Books, Open Library,
   or manual entry) and the external identifier within that catalog. This keeps
   the personal library record linked to its source without importing any
   public catalog data.

2. Modified Tables
   - `books`
     - `catalog_source` (text, nullable) — 'google_books' | 'open_library' | 'manual' | null
     - `external_book_id` (text, nullable) — the volume id / OL key / null for manual entries

3. Security
   - No policy changes. The new columns are covered by the existing owner-scoped
     RLS policies (full row access for the owning user).

4. Important Notes
   1. Both columns are nullable so existing manual entries and earlier rows
      remain valid.
   2. No data is lost — this is an additive migration only.
*/

ALTER TABLE books
  ADD COLUMN IF NOT EXISTS catalog_source text,
  ADD COLUMN IF NOT EXISTS external_book_id text;