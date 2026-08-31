import { useState } from 'react';
import {
  Search,
  Loader2,
  BookOpen,
  Plus,
  AlertCircle,
  ArrowLeft,
  Calendar,
  CheckCircle2,
} from 'lucide-react';
import { Modal } from './Modal';
import { StarRating } from './StarRating';
import { searchBooksPaged } from '@/lib/bookSearch';
import { addBook } from '@/lib/books';
import type { Book, BookSearchResult } from '@/lib/types';

interface AddBookModalProps {
  open: boolean;
  onClose: () => void;
  onAdded: (book: Book) => void;
}

type Mode = 'search' | 'manual';

const today = () => new Date().toISOString().slice(0, 10);

export function AddBookModal({ open, onClose, onAdded }: AddBookModalProps) {
  const [mode, setMode] = useState<Mode>('search');

  // Search state
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<BookSearchResult[]>([]);
  const [searching, setSearching] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [hasMore, setHasMore] = useState(false);
  const [nextStart, setNextStart] = useState(0);

  // Confirmation state — when a user picks a search result
  const [selected, setSelected] = useState<BookSearchResult | null>(null);
  const [confirmRating, setConfirmRating] = useState(0);
  const [confirmDate, setConfirmDate] = useState(today());
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  // Manual entry state
  const [form, setForm] = useState({
    title: '',
    author: '',
    cover: '',
    isbn: '',
    publicationYear: '',
    publisher: '',
    description: '',
    rating: 0,
    dateCompleted: today(),
  });
  const [manualSaving, setManualSaving] = useState(false);
  const [manualError, setManualError] = useState<string | null>(null);

  function reset() {
    setQuery('');
    setResults([]);
    setHasSearched(false);
    setHasMore(false);
    setNextStart(0);
    setSearchError(null);
    setSelected(null);
    setConfirmRating(0);
    setConfirmDate(today());
    setSaveError(null);
    setForm({
      title: '',
      author: '',
      cover: '',
      isbn: '',
      publicationYear: '',
      publisher: '',
      description: '',
      rating: 0,
      dateCompleted: today(),
    });
    setManualError(null);
    setMode('search');
  }

  function close() {
    reset();
    onClose();
  }

  async function runSearch(e: React.FormEvent) {
    e.preventDefault();
    if (!query.trim()) return;
    setSearching(true);
    setSearchError(null);
    setHasSearched(true);
    setResults([]);
    setHasMore(false);
    setNextStart(0);
    try {
      const page = await searchBooksPaged(query, 0);
      setResults(page.results);
      setHasMore(page.hasMore);
      setNextStart(page.nextStart);
    } catch (err) {
      setSearchError(err instanceof Error ? err.message : 'Search failed. Please try again.');
      setResults([]);
    } finally {
      setSearching(false);
    }
  }

  async function loadMore() {
    if (!query.trim() || loadingMore) return;
    setLoadingMore(true);
    setSearchError(null);
    try {
      const page = await searchBooksPaged(query, nextStart);
      setResults((prev) => dedupeResults([...prev, ...page.results]));
      setHasMore(page.hasMore);
      setNextStart(page.nextStart);
    } catch (err) {
      setSearchError(err instanceof Error ? err.message : 'Could not load more results.');
    } finally {
      setLoadingMore(false);
    }
  }

  function pickResult(r: BookSearchResult) {
    setSelected(r);
    setConfirmRating(0);
    setConfirmDate(today());
    setSaveError(null);
  }

  function backToResults() {
    setSelected(null);
    setSaveError(null);
  }

  async function saveConfirmed(e: React.FormEvent) {
    e.preventDefault();
    if (!selected) return;
    setSaving(true);
    setSaveError(null);
    try {
      const book = await addBook({
        title: selected.title,
        author: selected.author ?? null,
        cover: selected.cover ?? null,
        isbn: selected.isbn ?? null,
        publication_year: selected.publicationYear ?? null,
        publisher: selected.publisher ?? null,
        description: selected.description ?? null,
        rating: confirmRating,
        date_completed: confirmDate || null,
        catalog_source: selected.catalogSource ?? null,
        external_book_id: selected.externalBookId ?? null,
        categories: selected.categories ?? null,
      });
      onAdded(book);
      close();
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : 'Could not save your book. Please try again.');
    } finally {
      setSaving(false);
    }
  }

  async function saveManual(e: React.FormEvent) {
    e.preventDefault();
    if (!form.title.trim()) {
      setManualError('A book title is required.');
      return;
    }
    setManualSaving(true);
    setManualError(null);
    try {
      const book = await addBook({
        title: form.title.trim(),
        author: form.author.trim() || null,
        cover: form.cover.trim() || null,
        isbn: form.isbn.trim() || null,
        publication_year: form.publicationYear ? Number(form.publicationYear) : null,
        publisher: form.publisher.trim() || null,
        description: form.description.trim() || null,
        rating: form.rating,
        date_completed: form.dateCompleted || null,
        catalog_source: 'manual',
        external_book_id: null,
        categories: null,
      });
      onAdded(book);
      close();
    } catch (err) {
      setManualError(err instanceof Error ? err.message : 'Could not save your book. Please try again.');
    } finally {
      setManualSaving(false);
    }
  }

  return (
    <Modal
      open={open}
      onClose={close}
      title={selected ? 'Confirm Book' : 'Add a Book'}
      className="sm:max-w-2xl"
    >
      {selected ? (
        /* ---------------- Confirmation screen ---------------- */
        <form onSubmit={saveConfirmed} className="flex flex-col gap-5">
          <button
            type="button"
            onClick={backToResults}
            className="flex w-fit items-center gap-1.5 text-sm text-lavender-300/80 hover:text-lavender-200"
          >
            <ArrowLeft className="h-4 w-4" /> Back to results
          </button>

          <div className="flex flex-col gap-5 sm:flex-row">
            <div className="mx-auto w-36 shrink-0 sm:mx-0 sm:w-40">
              <div className="aspect-[2/3] overflow-hidden rounded-xl bg-midnight-900/60 shadow-card">
                {selected.cover ? (
                  <img src={selected.cover} alt={selected.title} className="h-full w-full object-cover" />
                ) : (
                  <div className="flex h-full flex-col items-center justify-center gap-2 bg-gradient-to-br from-midnight-700 to-midnight-900 p-3 text-center">
                    <BookOpen className="h-7 w-7 text-lavender-300/50" strokeWidth={1.5} />
                    <p className="font-serif text-xs text-cream-100/70">{selected.title}</p>
                  </div>
                )}
              </div>
            </div>

            <div className="flex min-w-0 flex-1 flex-col gap-3">
              <div>
                <h2 className="font-serif text-xl font-semibold leading-tight text-cream-50">
                  {selected.title}
                </h2>
                {selected.author && (
                  <p className="mt-0.5 text-sm text-lavender-200/80">by {selected.author}</p>
                )}
              </div>

              <dl className="grid grid-cols-2 gap-x-4 gap-y-2 text-sm">
                <Detail label="Published" value={selected.publicationYear ? String(selected.publicationYear) : '—'} />
                <Detail label="Publisher" value={selected.publisher ?? '—'} />
                <Detail label="ISBN" value={selected.isbn ?? '—'} />
                <Detail label="Source" value={sourceLabel(selected.catalogSource)} />
              </dl>

              {selected.categories && selected.categories.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {selected.categories.slice(0, 6).map((c) => (
                    <span
                      key={c}
                      className="rounded-full bg-lavender-500/15 px-2.5 py-1 text-[0.7rem] text-lavender-200 ring-1 ring-lavender-400/20"
                    >
                      {c}
                    </span>
                  ))}
                </div>
              )}

              {selected.description && (
                <p className="text-sm leading-relaxed text-midnight-100/70 line-clamp-4">
                  {selected.description}
                </p>
              )}
            </div>
          </div>

          {/* Rating + date */}
          <div className="grid grid-cols-1 gap-4 rounded-2xl bg-midnight-900/40 p-4 ring-1 ring-midnight-700/40 sm:grid-cols-2">
            <label className="flex flex-col gap-1.5">
              <span className="flex items-center gap-1.5 text-xs font-medium text-midnight-100/70">
                <StarIcon /> My Rating
              </span>
              <div className="flex h-[2.6rem] items-center">
                <StarRating value={confirmRating} onChange={setConfirmRating} size={28} />
              </div>
            </label>
            <label className="flex flex-col gap-1.5">
              <span className="flex items-center gap-1.5 text-xs font-medium text-midnight-100/70">
                <Calendar className="h-3.5 w-3.5" /> Date Completed
              </span>
              <input
                type="date"
                value={confirmDate}
                onChange={(e) => setConfirmDate(e.target.value)}
                className="input"
              />
            </label>
          </div>

          {saveError && (
            <div className="flex items-center gap-2 rounded-xl bg-red-500/10 px-3 py-2.5 text-sm text-red-300 ring-1 ring-red-500/20">
              <AlertCircle className="h-4 w-4 shrink-0" />
              {saveError}
            </div>
          )}

          <button
            type="submit"
            disabled={saving}
            className="flex items-center justify-center gap-2 rounded-xl bg-gradient-to-br from-lavender-500 to-midnight-600 px-4 py-3 text-sm font-semibold text-cream-50 shadow-glow transition-opacity disabled:opacity-50"
          >
            {saving ? <Loader2 className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
            Add to My Library
          </button>
        </form>
      ) : mode === 'search' ? (
        /* ---------------- Search mode ---------------- */
        <div className="flex flex-col gap-4">
          <form onSubmit={runSearch} className="flex gap-2">
            <div className="relative flex-1">
              <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-midnight-200/50" />
              <input
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search by book title, author or ISBN…"
                className="w-full rounded-xl border border-midnight-600/50 bg-midnight-900/50 py-2.5 pl-10 pr-3 text-sm text-cream-50 placeholder:text-midnight-200/40 focus:border-lavender-400/60 focus:outline-none focus:ring-2 focus:ring-lavender-400/20"
              />
            </div>
            <button
              type="submit"
              disabled={searching || !query.trim()}
              className="flex items-center gap-2 rounded-xl bg-gradient-to-br from-lavender-500 to-midnight-600 px-4 py-2.5 text-sm font-semibold text-cream-50 shadow-glow transition-opacity disabled:opacity-50"
            >
              {searching ? <Loader2 className="h-4 w-4 animate-spin" /> : <Search className="h-4 w-4" />}
              Search
            </button>
          </form>

          {searchError && (
            <div className="flex items-center gap-2 rounded-xl bg-red-500/10 px-3 py-2.5 text-sm text-red-300 ring-1 ring-red-500/20">
              <AlertCircle className="h-4 w-4 shrink-0" />
              {searchError}
            </div>
          )}

          {/* Results — no nested height cap; scrolls within the modal body */}
          {searching ? (
            <div className="flex flex-col items-center gap-3 py-12 text-midnight-100/60">
              <Loader2 className="h-7 w-7 animate-spin text-lavender-300" />
              <p className="text-sm">Searching the shelves…</p>
            </div>
          ) : hasSearched && results.length === 0 && !searchError ? (
            <div className="flex flex-col items-center gap-3 py-10 text-center">
              <BookOpen className="h-8 w-8 text-midnight-200/40" />
              <p className="text-sm text-midnight-100/60">No books found. Try another title or author.</p>
            </div>
          ) : !hasSearched ? (
            <div className="flex flex-col items-center gap-3 py-10 text-center">
              <BookOpen className="h-8 w-8 text-midnight-200/40" />
              <p className="text-sm text-midnight-100/50">
                Search for a book to add it to your library.
              </p>
            </div>
          ) : (
            <>
              <ul className="flex flex-col gap-2">
                {results.map((r) => (
                  <li
                    key={r.id}
                    className="flex gap-3 rounded-2xl bg-midnight-900/50 p-3 ring-1 ring-midnight-700/40 transition-colors hover:ring-lavender-400/30"
                  >
                    <div className="h-24 w-16 shrink-0 overflow-hidden rounded-lg bg-midnight-700/60">
                      {r.cover ? (
                        <img src={r.cover} alt={r.title} loading="lazy" className="h-full w-full object-cover" />
                      ) : (
                        <div className="flex h-full items-center justify-center">
                          <BookOpen className="h-5 w-5 text-midnight-200/40" />
                        </div>
                      )}
                    </div>
                    <div className="flex min-w-0 flex-1 flex-col gap-1">
                      <p className="font-serif text-base font-semibold text-cream-50 line-clamp-1">{r.title}</p>
                      {r.author && <p className="text-xs text-lavender-200/70 line-clamp-1">{r.author}</p>}
                      <p className="text-[0.7rem] text-midnight-200/50">
                        {[r.publicationYear, r.publisher, r.isbn && `ISBN ${r.isbn}`]
                          .filter(Boolean)
                          .join(' · ')}
                      </p>
                      <button
                        type="button"
                        onClick={() => pickResult(r)}
                        className="mt-1 flex w-fit items-center gap-1.5 rounded-lg bg-lavender-500/20 px-3 py-1.5 text-xs font-semibold text-lavender-200 ring-1 ring-lavender-400/30 transition-colors hover:bg-lavender-500/30"
                      >
                        <Plus className="h-3.5 w-3.5" /> Add to Library
                      </button>
                    </div>
                  </li>
                ))}
              </ul>

              {hasMore && (
                <button
                  type="button"
                  onClick={loadMore}
                  disabled={loadingMore}
                  className="flex items-center justify-center gap-2 rounded-xl border border-midnight-600/50 bg-midnight-900/40 px-4 py-3 text-sm font-medium text-cream-50 transition-colors hover:bg-midnight-800/60 disabled:opacity-50"
                >
                  {loadingMore ? <Loader2 className="h-4 w-4 animate-spin" /> : <Plus className="h-4 w-4" />}
                  Load More
                </button>
              )}
            </>
          )}

          <button
            type="button"
            onClick={() => setMode('manual')}
            className="text-center text-sm text-lavender-300/80 underline-offset-4 hover:text-lavender-200 hover:underline"
          >
            Can't find your book? Add manually
          </button>
        </div>
      ) : (
        /* ---------------- Manual entry mode ---------------- */
        <form onSubmit={saveManual} className="flex flex-col gap-4">
          <button
            type="button"
            onClick={() => setMode('search')}
            className="flex w-fit items-center gap-1.5 text-sm text-lavender-300/80 hover:text-lavender-200"
          >
            <ArrowLeft className="h-4 w-4" /> Back to search
          </button>

          <div className="flex flex-col gap-4 sm:grid sm:grid-cols-2">
            <Field label="Book title *">
              <input
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
                required
                className="input"
              />
            </Field>
            <Field label="Author">
              <input
                value={form.author}
                onChange={(e) => setForm({ ...form, author: e.target.value })}
                className="input"
              />
            </Field>
            <Field label="Cover image URL">
              <input
                value={form.cover}
                onChange={(e) => setForm({ ...form, cover: e.target.value })}
                placeholder="https://…"
                className="input"
              />
            </Field>
            <Field label="ISBN">
              <input
                value={form.isbn}
                onChange={(e) => setForm({ ...form, isbn: e.target.value })}
                className="input"
              />
            </Field>
            <Field label="Publication year">
              <input
                value={form.publicationYear}
                onChange={(e) => setForm({ ...form, publicationYear: e.target.value })}
                inputMode="numeric"
                className="input"
              />
            </Field>
            <Field label="Publisher">
              <input
                value={form.publisher}
                onChange={(e) => setForm({ ...form, publisher: e.target.value })}
                className="input"
              />
            </Field>
            <Field label="Date completed">
              <input
                type="date"
                value={form.dateCompleted}
                onChange={(e) => setForm({ ...form, dateCompleted: e.target.value })}
                className="input"
              />
            </Field>
            <Field label="Your rating">
              <div className="flex h-[2.6rem] items-center">
                <StarRating
                  value={form.rating}
                  onChange={(v) => setForm({ ...form, rating: v })}
                  size={26}
                />
              </div>
            </Field>
          </div>
          <Field label="Description">
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              rows={3}
              className="input resize-none"
            />
          </Field>

          {manualError && (
            <div className="flex items-center gap-2 rounded-xl bg-red-500/10 px-3 py-2.5 text-sm text-red-300 ring-1 ring-red-500/20">
              <AlertCircle className="h-4 w-4 shrink-0" />
              {manualError}
            </div>
          )}

          <div className="flex gap-3 pt-1">
            <button
              type="button"
              onClick={close}
              className="flex-1 rounded-xl border border-midnight-600/50 px-4 py-2.5 text-sm font-medium text-midnight-100/80 transition-colors hover:bg-midnight-700/40"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={manualSaving}
              className="flex flex-[1.5] items-center justify-center gap-2 rounded-xl bg-gradient-to-br from-lavender-500 to-midnight-600 px-4 py-2.5 text-sm font-semibold text-cream-50 shadow-glow transition-opacity disabled:opacity-50"
            >
              {manualSaving ? <Loader2 className="h-4 w-4 animate-spin" /> : <Plus className="h-4 w-4" />}
              Save Book
            </button>
          </div>
        </form>
      )}
    </Modal>
  );
}

function dedupeResults(results: BookSearchResult[]): BookSearchResult[] {
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

function sourceLabel(source: string | null): string {
  switch (source) {
    case 'google_books':
      return 'Google Books';
    case 'open_library':
      return 'Open Library';
    case 'manual':
      return 'Manual entry';
    default:
      return '—';
  }
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="flex flex-col gap-1.5">
      <span className="text-xs font-medium text-midnight-100/70">{label}</span>
      {children}
    </label>
  );
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-[0.7rem] uppercase tracking-wide text-midnight-200/40">{label}</dt>
      <dd className="text-cream-50/90">{value}</dd>
    </div>
  );
}

function StarIcon() {
  return (
    <svg className="h-3.5 w-3.5" viewBox="0 0 24 24" fill="currentColor" aria-hidden>
    <path d="M12 2l2.9 6.9 7.1.6-5.4 4.7 1.6 7L12 17.8 5.8 21.2l1.6-7L2 9.5l7.1-.6L12 2z" />
  </svg>
  );
}
