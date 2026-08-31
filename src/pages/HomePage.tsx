import { Plus, BookOpen, Star, Library as LibraryIcon } from 'lucide-react';
import { Starfield } from '@/components/Starfield';
import type { Book, ViewName } from '@/lib/types';

interface HomePageProps {
  books: Book[];
  userName: string | null;
  onAddBook: () => void;
  onNavigate: (view: ViewName) => void;
}

export function HomePage({ books, userName, onAddBook, onNavigate }: HomePageProps) {
  const completed = books.length;
  const rated = books.filter((b) => b.rating > 0);
  const avgRating = rated.length
    ? rated.reduce((sum, b) => sum + b.rating, 0) / rated.length
    : 0;

  return (
    <div className="relative mx-auto w-full max-w-5xl px-4 pb-28 pt-8 sm:px-6 lg:pb-12">
      {/* Hero */}
      <section className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-midnight-800/80 via-midnight-900/70 to-midnight-950 px-6 py-12 shadow-soft ring-1 ring-midnight-700/40 sm:px-10 sm:py-16">
        <Starfield count={28} />
        <div className="relative z-10 flex flex-col items-center text-center">
          <div className="mb-4 grid h-16 w-16 place-items-center rounded-2xl bg-gradient-to-br from-lavender-500/30 to-midnight-700/40 ring-1 ring-lavender-400/30">
            <BookOpen className="h-8 w-8 text-lavender-200" strokeWidth={1.5} />
          </div>
          <p className="font-sans text-xs uppercase tracking-[0.3em] text-lavender-300/70">
            {userName ? `Welcome back, ${firstName(userName)}` : 'Welcome to'}
          </p>
          <h1 className="mt-1 font-serif text-4xl font-semibold text-cream-50 sm:text-5xl">
            Blue Hour Books
          </h1>
          <p className="mt-4 max-w-md font-serif text-lg italic text-cream-100/70 sm:text-xl">
            Every finished book becomes a little part of your story.
          </p>
          <button
            type="button"
            onClick={onAddBook}
            className="mt-8 flex items-center gap-2 rounded-full bg-gradient-to-br from-lavender-500 to-midnight-600 px-7 py-3.5 font-sans text-sm font-semibold text-cream-50 shadow-glow transition-transform hover:scale-105 active:scale-95"
          >
            <Plus className="h-5 w-5" /> Add Book
          </button>
        </div>
      </section>

      {/* Stats */}
      <section className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard
          icon={<BookOpen className="h-5 w-5" />}
          label="Books Completed"
          value={String(completed)}
          accent="from-lavender-500/20 to-lavender-600/5"
        />
        <StatCard
          icon={<Star className="h-5 w-5" />}
          label="Average Rating"
          value={avgRating > 0 ? avgRating.toFixed(1) : '—'}
          accent="from-gold-500/20 to-gold-600/5"
        />
        <StatCard
          icon={<LibraryIcon className="h-5 w-5" />}
          label="In Your Library"
          value={String(completed)}
          accent="from-midnight-400/20 to-midnight-500/5"
        />
      </section>

      {/* Recent books */}
      {books.length > 0 && (
        <section className="mt-10">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="font-serif text-2xl font-semibold text-cream-50">Recently Added</h2>
            <button
              type="button"
              onClick={() => onNavigate('library')}
              className="text-sm font-medium text-lavender-300/80 hover:text-lavender-200"
            >
              View all →
            </button>
          </div>
          <p className="text-sm text-midnight-100/50">Your latest finished reads.</p>
        </section>
      )}
    </div>
  );
}

function firstName(name: string): string {
  return name.split(' ')[0] || name;
}

function StatCard({
  icon,
  label,
  value,
  accent,
}: {
  icon: React.ReactNode;
  label: string;
  value: string;
  accent: string;
}) {
  return (
    <div className={`relative overflow-hidden rounded-2xl bg-gradient-to-br ${accent} p-5 ring-1 ring-midnight-700/40`}>
      <div className="mb-3 grid h-10 w-10 place-items-center rounded-xl bg-midnight-900/50 text-lavender-200 ring-1 ring-midnight-700/40">
        {icon}
      </div>
      <p className="font-serif text-3xl font-semibold text-cream-50">{value}</p>
      <p className="mt-0.5 text-xs uppercase tracking-wide text-midnight-100/60">{label}</p>
    </div>
  );
}
