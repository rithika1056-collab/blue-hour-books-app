import { useMemo, useState } from 'react';
import { Search, Library as LibraryIcon, ArrowDownUp, BookOpen } from 'lucide-react';
import { BookCard } from '@/components/BookCard';
import type { Book, SortKey } from '@/lib/types';
import { cn } from '@/lib/utils';

interface LibraryPageProps {
  books: Book[];
  onOpenBook: (book: Book) => void;
  onAddBook: () => void;
}

const SORT_OPTIONS: { key: SortKey; label: string }[] = [
  { key: 'date_completed', label: 'Date Completed' },
  { key: 'title', label: 'Title' },
  { key: 'author', label: 'Author' },
  { key: 'rating', label: 'Rating' },
];

export function LibraryPage({ books, onOpenBook, onAddBook }: LibraryPageProps) {
  const [query, setQuery] = useState('');
  const [sort, setSort] = useState<SortKey>('date_completed');
  const [sortOpen, setSortOpen] = useState(false);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    let list = books;
    if (q) {
      list = books.filter(
        (b) =>
          b.title.toLowerCase().includes(q) ||
          (b.author?.toLowerCase().includes(q) ?? false) ||
          (b.isbn?.toLowerCase().includes(q) ?? false)
      );
    }
    const sorted = [...list].sort((a, b) => {
      switch (sort) {
        case 'title':
          return a.title.localeCompare(b.title);
        case 'author':
          return (a.author ?? '').localeCompare(b.author ?? '');
        case 'rating':
          return b.rating - a.rating;
        case 'date_completed':
        default: {
          const ad = a.date_completed ? new Date(a.date_completed).getTime() : 0;
          const bd = b.date_completed ? new Date(b.date_completed).getTime() : 0;
          return bd - ad;
        }
      }
    });
    return sorted;
  }, [books, query, sort]);

  return (
    <div className="mx-auto w-full max-w-6xl px-4 pb-28 pt-8 sm:px-6 lg:pb-12">
      <header className="mb-6 flex flex-col gap-1">
        <h1 className="font-serif text-3xl font-semibold text-cream-50 sm:text-4xl">Library</h1>
        <p className="text-sm text-midnight-100/60">
          {books.length === 0
            ? 'Your personal collection of finished books.'
            : `${filtered.length} ${filtered.length === 1 ? 'book' : 'books'} shown`}
        </p>
      </header>

      {/* Controls */}
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center">
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-midnight-200/50" />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search your library…"
            className="w-full rounded-xl border border-midnight-600/50 bg-midnight-900/50 py-2.5 pl-10 pr-3 text-sm text-cream-50 placeholder:text-midnight-200/40 focus:border-lavender-400/60 focus:outline-none focus:ring-2 focus:ring-lavender-400/20"
          />
        </div>
        <div className="relative">
          <button
            type="button"
            onClick={() => setSortOpen((v) => !v)}
            onBlur={() => setTimeout(() => setSortOpen(false), 150)}
            className="flex w-full items-center justify-between gap-2 rounded-xl border border-midnight-600/50 bg-midnight-900/50 px-4 py-2.5 text-sm font-medium text-cream-50 transition-colors hover:bg-midnight-800/50 sm:w-auto"
          >
            <ArrowDownUp className="h-4 w-4 text-lavender-300" />
            {SORT_OPTIONS.find((o) => o.key === sort)?.label}
          </button>
          {sortOpen && (
            <ul className="absolute right-0 z-20 mt-1.5 w-48 overflow-hidden rounded-xl bg-midnight-800 py-1 shadow-soft ring-1 ring-midnight-600/50">
              {SORT_OPTIONS.map((o) => (
                <li key={o.key}>
                  <button
                    type="button"
                    onClick={() => {
                      setSort(o.key);
                      setSortOpen(false);
                    }}
                    className={cn(
                      'flex w-full px-4 py-2 text-left text-sm transition-colors hover:bg-midnight-700/60',
                      sort === o.key ? 'text-lavender-200' : 'text-midnight-100/70'
                    )}
                  >
                    {o.label}
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {/* Grid / Empty */}
      {books.length === 0 ? (
        <EmptyState onAddBook={onAddBook} />
      ) : filtered.length === 0 ? (
        <div className="flex flex-col items-center gap-3 py-16 text-center">
          <BookOpen className="h-8 w-8 text-midnight-200/40" />
          <p className="text-sm text-midnight-100/60">No books match your search.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6">
          {filtered.map((book) => (
            <BookCard key={book.id} book={book} onClick={onOpenBook} />
          ))}
        </div>
      )}
    </div>
  );
}

function EmptyState({ onAddBook }: { onAddBook: () => void }) {
  return (
    <div className="flex flex-col items-center gap-5 rounded-3xl bg-midnight-800/40 px-6 py-16 text-center ring-1 ring-midnight-700/40">
      <div className="grid h-20 w-20 place-items-center rounded-full bg-gradient-to-br from-lavender-500/20 to-midnight-700/30 ring-1 ring-lavender-400/20">
        <LibraryIcon className="h-10 w-10 text-lavender-200/80" strokeWidth={1.5} />
      </div>
      <div>
        <h2 className="font-serif text-2xl font-semibold text-cream-50">Your shelf is waiting</h2>
        <p className="mt-1.5 max-w-sm text-sm text-midnight-100/60">
          You haven't added any books yet. Search for a book you've finished and start building your library.
        </p>
      </div>
      <button
        type="button"
        onClick={onAddBook}
        className="flex items-center gap-2 rounded-full bg-gradient-to-br from-lavender-500 to-midnight-600 px-6 py-3 text-sm font-semibold text-cream-50 shadow-glow transition-transform hover:scale-105"
      >
        Add your first book
      </button>
    </div>
  );
}
