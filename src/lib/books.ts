import { supabase } from './supabase';
import type { Book, BookInput } from './types';

export async function fetchBooks(): Promise<Book[]> {
  const { data, error } = await supabase
    .from('books')
    .select('*')
    .order('created_at', { ascending: false });
  if (error) throw error;
  return (data ?? []) as Book[];
}

export async function addBook(input: BookInput): Promise<Book> {
  const payload = {
    title: input.title,
    author: input.author ?? null,
    cover: input.cover ?? null,
    isbn: input.isbn ?? null,
    publication_year: input.publication_year ?? null,
    publisher: input.publisher ?? null,
    description: input.description ?? null,
    rating: input.rating ?? 0,
    date_completed: input.date_completed ?? null,
    catalog_source: input.catalog_source ?? null,
    external_book_id: input.external_book_id ?? null,
    categories: input.categories ?? null,
  };
  const { data, error } = await supabase
    .from('books')
    .insert(payload)
    .select()
    .single();
  if (error) throw error;
  return data as Book;
}

export async function updateBook(id: string, input: Partial<BookInput>): Promise<Book> {
  const { data, error } = await supabase
    .from('books')
    .update({ ...input, updated_at: new Date().toISOString() })
    .eq('id', id)
    .select()
    .single();
  if (error) throw error;
  return data as Book;
}

export async function deleteBook(id: string): Promise<void> {
  const { error } = await supabase.from('books').delete().eq('id', id);
  if (error) throw error;
}
