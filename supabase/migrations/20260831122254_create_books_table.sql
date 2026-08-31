/*
# Create books table for Blue Hour Books

1. Purpose
   Stores each user's personal completed-book library. Each user only sees
   and manages their own books — this is enforced by Row Level Security.

2. New Tables
   - `books`
     - `id` (uuid, primary key, auto-generated)
     - `user_id` (uuid, not null, defaults to the authenticated user, references auth.users with cascade delete)
     - `title` (text, not null)
     - `author` (text, nullable)
     - `cover` (text, nullable — URL to cover image)
     - `isbn` (text, nullable)
     - `publication_year` (int4, nullable)
     - `publisher` (text, nullable)
     - `description` (text, nullable)
     - `rating` (numeric(2,1), not null, default 0, constrained 0–5)
     - `date_completed` (date, nullable)
     - `created_at` (timestamptz, default now)
     - `updated_at` (timestamptz, default now)

3. Indexes
   - `idx_books_user_id` on `user_id` for fast per-user queries
   - `idx_books_user_date` on `(user_id, date_completed)` for date-sorted library views

4. Security
   - Enable RLS on `books`.
   - Four owner-scoped policies (SELECT, INSERT, UPDATE, DELETE) scoped to `authenticated`
     using `auth.uid() = user_id`.
   - The `user_id` column defaults to `auth.uid()` so client inserts that omit
     `user_id` still satisfy the INSERT WITH CHECK policy.

5. Important Notes
   1. No data is seeded — a new user's library starts empty.
   2. `rating` accepts half-star values (0, 0.5, 1, 1.5, … 5) via numeric(2,1).
   3. `ON DELETE CASCADE` on the user_id FK removes a user's books when their
      auth account is deleted.
*/

CREATE TABLE IF NOT EXISTS books (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL DEFAULT auth.uid() REFERENCES auth.users(id) ON DELETE CASCADE,
  title text NOT NULL,
  author text,
  cover text,
  isbn text,
  publication_year int4,
  publisher text,
  description text,
  rating numeric(2,1) NOT NULL DEFAULT 0 CHECK (rating >= 0 AND rating <= 5),
  date_completed date,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_books_user_id ON books(user_id);
CREATE INDEX IF NOT EXISTS idx_books_user_date ON books(user_id, date_completed);

ALTER TABLE books ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "select_own_books" ON books;
CREATE POLICY "select_own_books" ON books FOR SELECT
  TO authenticated USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "insert_own_books" ON books;
CREATE POLICY "insert_own_books" ON books FOR INSERT
  TO authenticated WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "update_own_books" ON books;
CREATE POLICY "update_own_books" ON books FOR UPDATE
  TO authenticated USING (auth.uid() = user_id) WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "delete_own_books" ON books;
CREATE POLICY "delete_own_books" ON books FOR DELETE
  TO authenticated USING (auth.uid() = user_id);