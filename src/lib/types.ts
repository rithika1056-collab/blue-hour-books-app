export interface Book {
  id: string;
  user_id: string;
  title: string;
  author: string | null;
  cover: string | null;
  isbn: string | null;
  publication_year: number | null;
  publisher: string | null;
  description: string | null;
  rating: number;
  date_completed: string | null;
  catalog_source: string | null;
  external_book_id: string | null;
  categories: string[] | null;
  created_at: string;
  updated_at: string;
}

export type BookInput = Omit<Book, 'id' | 'user_id' | 'created_at' | 'updated_at'>;

export interface BookSearchResult {
  id: string;
  externalBookId: string | null;
  catalogSource: string | null;
  title: string;
  author: string | null;
  cover: string | null;
  isbn: string | null;
  publicationYear: number | null;
  publisher: string | null;
  description: string | null;
  categories: string[] | null;
}

export type SortKey = 'title' | 'author' | 'rating' | 'date_completed';

export type ViewName = 'home' | 'add' | 'library' | 'bookshelf' | 'profile';
