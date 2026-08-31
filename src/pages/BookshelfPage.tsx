import { useMemo } from 'react';
import { BookMarked } from 'lucide-react';
import type { Book } from '@/lib/types';

interface BookshelfPageProps {
  books: Book[];
  onOpenBook: (book: Book) => void;
  onAddBook: () => void;
}

// Deterministic pseudo-random per book id, for spine variety
function hash(str: string): number {
  let h = 0;
  for (let i = 0; i < str.length; i++) h = (Math.imul(31, h) + str.charCodeAt(i)) | 0;
  return Math.abs(h);
}

const SPINE_COLORS = [
  'from-lavender-500 to-lavender-700',
  'from-midnight-400 to-midnight-600',
  'from-gold-500 to-gold-600',
  'from-midnight-500 to-midnight-700',
  'from-lavender-400 to-midnight-600',
  'from-gold-400 to-gold-600',
];

export function BookshelfPage({ books, onOpenBook, onAddBook }: BookshelfPageProps) {
  const shelves = useMemo(() => {
    const sorted = [...books].sort((a, b) => a.title.localeCompare(b.title));
    const perShelf = 8;
    const out: Book[][] = [];
    for (let i = 0; i < sorted.length; i += perShelf) {
      out.push(sorted.slice(i, i + perShelf));
    }
    return out;
  }, [books]);

  return (
    <div className="mx-auto w-full max-w-6xl px-4 pb-28 pt-8 sm:px-6 lg:pb-12">
      <header className="mb-6">
        <h1 className="font-serif text-3xl font-semibold text-cream-50 sm:text-4xl">Bookshelf</h1>
        <p className="text-sm text-midnight-100/60">
          {books.length === 0
            ? 'Your finished reads, lined up like a cozy shelf.'
            : `${books.length} ${books.length === 1 ? 'book' : 'books'} on your shelf`}
        </p>
      </header>

      {books.length === 0 ? (
        <div className="flex flex-col items-center gap-5 rounded-3xl bg-midnight-800/40 px-6 py-16 text-center ring-1 ring-midnight-700/40">
          <div className="grid h-20 w-20 place-items-center rounded-full bg-gradient-to-br from-lavender-500/20 to-midnight-700/30 ring-1 ring-lavender-400/20">
            <BookMarked className="h-10 w-10 text-lavender-200/80" strokeWidth={1.5} />
          </div>
          <div>
            <h2 className="font-serif text-2xl font-semibold text-cream-50">An empty shelf</h2>
            <p className="mt-1.5 max-w-sm text-sm text-midnight-100/60">
              Add a book you've finished and watch your bookshelf come to life.
            </p>
          </div>
          <button
            type="button"
            onClick={onAddBook}
            className="flex items-center gap-2 rounded-full bg-gradient-to-br from-lavender-500 to-midnight-600 px-6 py-3 text-sm font-semibold text-cream-50 shadow-glow transition-transform hover:scale-105"
          >
            Add a book
          </button>
        </div>
      ) : (
        <div className="flex flex-col gap-6">
          {shelves.map((shelf, si) => (
            <div key={si} className="relative">
              {/* Shelf board */}
              <div className="flex items-end justify-center gap-1.5 overflow-x-auto px-1 pb-2 sm:gap-2.5">
                {shelf.map((book) => {
                  const h = hash(book.id);
                  const height = 180 + (h % 70); // 180–249px
                  const width = 42 + (h % 16); // 42–57px
                  const color = SPINE_COLORS[h % SPINE_COLORS.length];
                  const tilt = (h % 5) - 2; // -2..2 degrees
                  return (
                    <button
                      key={book.id}
                      type="button"
                      onClick={() => onOpenBook(book)}
                      title={book.title}
                      className="group relative shrink-0"
                      style={{ height, transform: `rotate(${tilt}deg)` }}
                    >
                      {book.cover ? (
                        <div
                          className="h-full overflow-hidden rounded-md shadow-card ring-1 ring-black/20 transition-transform group-hover:-translate-y-1.5 group-hover:shadow-soft"
                          style={{ width }}
                        >
                          <img
                            src={book.cover}
                            alt={book.title}
                            loading="lazy"
                            className="h-full w-full object-cover"
                          />
                        </div>
                      ) : (
                        <div
                          className={`flex h-full flex-col items-center justify-end gap-1 rounded-md bg-gradient-to-b ${color} p-1.5 text-center shadow-card ring-1 ring-black/20 transition-transform group-hover:-translate-y-1.5 group-hover:shadow-soft`}
                          style={{ width }}
                        >
                          <div className="flex-1" />
                          <div className="h-px w-full bg-cream-100/30" />
                          <p
                            className="font-serif text-[0.6rem] font-medium leading-tight text-cream-50/90"
                            style={{ writingMode: 'vertical-rl' }}
                          >
                            {book.title}
                          </p>
                        </div>
                      )}
                    </button>
                  );
                })}
              </div>
              {/* Wood plank */}
              <div className="h-3 rounded-sm bg-gradient-to-b from-cream-500/30 to-cream-600/20 shadow-[0_4px_12px_-4px_rgba(0,0,0,0.5)]" />
              <div className="h-1 bg-cream-600/10" />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
