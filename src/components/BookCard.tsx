import { BookOpen } from 'lucide-react';
import type { Book } from '@/lib/types';
import { StarRating } from './StarRating';
import { cn } from '@/lib/utils';

interface BookCardProps {
  book: Book;
  onClick?: (book: Book) => void;
  className?: string;
}

export function BookCard({ book, onClick, className }: BookCardProps) {
  return (
    <button
      type="button"
      onClick={() => onClick?.(book)}
      className={cn(
        'group flex flex-col overflow-hidden rounded-2xl bg-midnight-800/60 text-left shadow-card ring-1 ring-midnight-700/40 transition-all hover:-translate-y-1 hover:ring-lavender-400/40 hover:shadow-soft',
        className
      )}
    >
      <div className="relative aspect-[2/3] w-full overflow-hidden bg-midnight-900/60">
        {book.cover ? (
          <img
            src={book.cover}
            alt={book.title}
            loading="lazy"
            className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
          />
        ) : (
          <div className="flex h-full w-full flex-col items-center justify-center gap-2 bg-gradient-to-br from-midnight-700 to-midnight-900 p-4 text-center">
            <BookOpen className="h-8 w-8 text-lavender-300/60" strokeWidth={1.5} />
            <p className="font-serif text-sm text-cream-100/80 line-clamp-3">{book.title}</p>
          </div>
        )}
        <div className="absolute inset-x-0 bottom-0 h-16 bg-gradient-to-t from-midnight-900/80 to-transparent" />
      </div>
      <div className="flex flex-1 flex-col gap-1.5 p-3.5">
        <h3 className="font-serif text-base font-semibold leading-snug text-cream-50 line-clamp-2">
          {book.title}
        </h3>
        {book.author && (
          <p className="font-sans text-xs text-lavender-200/70 line-clamp-1">{book.author}</p>
        )}
        <div className="mt-auto pt-1.5">
          <StarRating value={book.rating} readOnly size={15} />
        </div>
        {book.date_completed && (
          <p className="font-sans text-[0.7rem] text-midnight-200/50">
            Completed {formatDate(book.date_completed)}
          </p>
        )}
      </div>
    </button>
  );
}

export function formatDate(iso: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}
