import type { BookSearchResult } from './types';

/*
 * Real book-search service.
 *
 * Primary source: Google Books API (no API key required for public search).
 * Fallback source: Open Library Search API (no API key required).
 *
 * Results from both sources are normalized, merged, and de-duplicated by
 * ISBN (preferred) or title+author signature. Author searches are detected
 * and routed to the appropriate query form so that searching an author name
 * returns many books by that author.
 */

const GOOGLE_BOOKS_ENDPOINT = 'https://www.googleapis.com/books/v1/volumes';
const OPEN_LIBRARY_ENDPOINT = 'https://openlibrary.org/search.json';

const PAGE_SIZE = 20;
const MAX_PER_SOURCE = 40;

export interface SearchPage {
  results: BookSearchResult[];
  /** Index of the next item to fetch (for Google Books `startIndex` / OL `offset`). */
  nextStart: number;
  /** Whether more results may be available. */
  hasMore: boolean;
}

/** Detects whether a query looks like an ISBN (10 or 13 digits, with optional hyphens). */
export function isIsbn(query: string): boolean {
  const cleaned = query.replace(/[-\s]/g, '');
  return /^\d{9,13}$/.test(cleaned) && (cleaned.length === 10 || cleaned.length === 13);
}

/**
 * Runs a single page of search across Google Books (primary) and Open Library
 * (fallback / supplement). Combines and de-duplicates the results.
 *
 * @param query  The user's search text.
 * @param start  Zero-based offset for pagination (used for Load More).
 */
export async function searchBooksPaged(query: string, start = 0): Promise<SearchPage> {
  const trimmed = query.trim();
  if (!trimmed) return { results: [], nextStart: 0, hasMore: false };

  let googleResults: BookSearchResult[] = [];
  let googleHasMore = false;
  let googleError: unknown = null;

  try {
    const g = await searchGoogleBooks(trimmed, start);
    googleResults = g.results;
    googleHasMore = g.hasMore;
  } catch (err) {
    googleError = err;
  }

  // Fetch a page from Open Library to supplement (or as fallback if Google failed).
  let olResults: BookSearchResult[] = [];
  let olHasMore = false;
  try {
    const ol = await searchOpenLibrary(trimmed, start);
    olResults = ol.results;
    olHasMore = ol.hasMore;
  } catch {
    // If both fail, surface the Google error below.
  }

  if (googleResults.length === 0 && olResults.length === 0) {
    if (googleError) {
      throw new Error(
        'Search failed. Please try again.'
      );
    }
    return { results: [], nextStart: start, hasMore: false };
  }

  const merged = dedupe([...googleResults, ...olResults]);
  const pageResults = merged.slice(0, PAGE_SIZE);
  const hasMore = googleHasMore || olHasMore || merged.length > PAGE_SIZE;

  return {
    results: pageResults,
    nextStart: start + pageResults.length,
    hasMore,
  };
}

/* ------------------------------------------------------------------ */
/* Google Books                                                        */
/* ------------------------------------------------------------------ */

async function searchGoogleBooks(
  query: string,
  start: number
): Promise<{ results: BookSearchResult[]; hasMore: boolean }> {
  const queries = buildGoogleQueries(query);
  const maxResults = Math.min(PAGE_SIZE, MAX_PER_SOURCE);
  const responses = await Promise.allSettled(
    queries.map(async (q) => {
      const url = `${GOOGLE_BOOKS_ENDPOINT}?q=${encodeURIComponent(q)}&startIndex=${start}&maxResults=${maxResults}`;
      const res = await fetch(url);
      if (!res.ok) throw new Error(`Google Books request failed (${res.status})`);
      return (await res.json()) as GoogleBooksResponse;
    })
  );

  const successful = responses
    .filter((result): result is PromiseFulfilledResult<GoogleBooksResponse> => result.status === 'fulfilled')
    .map((result) => result.value);

  if (successful.length === 0) {
    throw new Error('Google Books search failed');
  }

  const items = successful.flatMap((data) => data.items ?? []);
  const results = items
    .map((item) => normalizeGoogleItem(item))
    .filter((r): r is BookSearchResult => r !== null);

  const hasMore = successful.some((data) => {
    const total = data.totalItems ?? 0;
    return start + (data.items?.length ?? 0) < total;
  });

  return { results, hasMore };
}

function buildGoogleQueries(query: string): string[] {
  if (isIsbn(query)) {
    return [`isbn:${query.replace(/[-\s]/g, '')}`];
  }

  const escaped = query.replace(/"/g, '').trim();
  if (escaped.split(/\s+/).length >= 2) {
    return [query, `inauthor:"${escaped}"`, `intitle:"${escaped}"`];
  }

  return [query];
}

function normalizeGoogleItem(item: GoogleBooksItem): BookSearchResult | null {
  const info = item.volumeInfo;
  if (!info || !info.title) return null;

  const authors = info.authors ?? [];
  const author = authors.length ? authors.join(', ') : null;

  const industryIds = info.industryIdentifiers ?? [];
  const isbn =
    industryIds.find((i) => i.type === 'ISBN_13')?.identifier ??
    industryIds.find((i) => i.type === 'ISBN_10')?.identifier ??
    null;

  const cover =
    info.imageLinks?.thumbnail ??
    info.imageLinks?.smallThumbnail ??
    info.imageLinks?.small ??
    info.imageLinks?.medium ??
    info.imageLinks?.large ??
    null;
  // Upgrade thumbnail to a larger, cleaner image.
  const coverUrl = cover ? cover.replace(/^http:/, 'https:') : null;

  const year = info.publishedDate ? extractYear(info.publishedDate) : null;

  return {
    id: `gb:${item.id}`,
    externalBookId: item.id,
    catalogSource: 'google_books',
    title: info.title,
    author,
    cover: coverUrl,
    isbn,
    publicationYear: year,
    publisher: info.publisher ?? null,
    description: info.description ?? null,
    categories: info.categories ?? null,
  };
}

/* ------------------------------------------------------------------ */
/* Open Library                                                        */
/* ------------------------------------------------------------------ */

async function searchOpenLibrary(
  query: string,
  start: number
): Promise<{ results: BookSearchResult[]; hasMore: boolean }> {
  const limit = Math.min(PAGE_SIZE, MAX_PER_SOURCE);
  const offset = start;

  let q: string;
  if (isIsbn(query)) {
    q = `isbn:${query.replace(/[-\s]/g, '')}`;
  } else {
    q = query;
  }

  const fields =
    'key,title,author_name,cover_i,first_publish_year,publisher,isbn,description,subject';
  const url = `${OPEN_LIBRARY_ENDPOINT}?q=${encodeURIComponent(q)}&limit=${limit}&offset=${offset}&fields=${fields}`;

  const res = await fetch(url);
  if (!res.ok) throw new Error(`Open Library request failed (${res.status})`);
  const data = (await res.json()) as OpenLibraryResponse;

  const docs = data.docs ?? [];
  const results: BookSearchResult[] = docs
    .map((d) => normalizeOpenLibraryDoc(d))
    .filter((r): r is BookSearchResult => r !== null);

  const total = data.numFound ?? results.length;
  const hasMore = start + results.length < total && results.length > 0;

  return { results, hasMore };
}

function normalizeOpenLibraryDoc(doc: OpenLibraryDoc): BookSearchResult | null {
  if (!doc.title) return null;

  const isbns = doc.isbn ?? [];
  const isbn = isbns.length ? isbns[0] : null;

  const description =
    typeof doc.description === 'string'
      ? doc.description
      : doc.description?.value ?? null;

  const cover = doc.cover_i
    ? `https://covers.openlibrary.org/b/id/${doc.cover_i}-M.jpg`
    : null;

  return {
    id: `ol:${doc.key}`,
    externalBookId: doc.key,
    catalogSource: 'open_library',
    title: doc.title,
    author: doc.author_name?.[0] ?? null,
    cover,
    isbn,
    publicationYear: doc.first_publish_year ?? null,
    publisher: doc.publisher?.[0] ?? null,
    description,
    categories: doc.subject?.slice(0, 6) ?? null,
  };
}

/* ------------------------------------------------------------------ */
/* Helpers                                                             */
/* ------------------------------------------------------------------ */

function dedupe(results: BookSearchResult[]): BookSearchResult[] {
  const byIsbn = new Map<string, BookSearchResult>();
  const bySig = new Map<string, BookSearchResult>();
  const out: BookSearchResult[] = [];

  for (const r of results) {
    if (r.isbn) {
      const key = r.isbn.replace(/[-\s]/g, '');
      if (byIsbn.has(key)) continue;
      byIsbn.set(key, r);
      out.push(r);
    } else {
      const sig = `${r.title.toLowerCase()}|${(r.author ?? '').toLowerCase()}`;
      if (bySig.has(sig)) continue;
      bySig.set(sig, r);
      out.push(r);
    }
  }
  return out;
}

function extractYear(dateStr: string): number | null {
  const m = dateStr.match(/(\d{4})/);
  return m ? Number(m[1]) : null;
}

/* ------------------------------------------------------------------ */
/* Types                                                               */
/* ------------------------------------------------------------------ */

interface GoogleBooksResponse {
  totalItems?: number;
  items?: GoogleBooksItem[];
}

interface GoogleBooksItem {
  id: string;
  volumeInfo: {
    title?: string;
    authors?: string[];
    publisher?: string;
    publishedDate?: string;
    description?: string;
    industryIdentifiers?: { type: string; identifier: string }[];
    imageLinks?: {
      smallThumbnail?: string;
      thumbnail?: string;
      small?: string;
      medium?: string;
      large?: string;
    };
    categories?: string[];
  };
}

interface OpenLibraryResponse {
  numFound?: number;
  docs?: OpenLibraryDoc[];
}

interface OpenLibraryDoc {
  key: string;
  title?: string;
  author_name?: string[];
  cover_i?: number;
  first_publish_year?: number;
  publisher?: string[];
  isbn?: string[];
  description?: string | { value?: string };
  subject?: string[];
}

/** Returns a larger cover URL when the source supports it. */
export function largeCover(coverUrl: string | null): string | null {
  if (!coverUrl) return null;
  if (coverUrl.includes('covers.openlibrary.org')) {
    return coverUrl.replace('/M.jpg', '/L.jpg');
  }
  if (coverUrl.includes('googleusercontent.com') || coverUrl.includes('books.google.com')) {
    return coverUrl.replace(/&zoom=\d/, '&zoom=0');
  }
  return coverUrl;
}
