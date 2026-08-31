import { useState } from 'react';
import { LogOut, BookOpen, Star, Loader2 } from 'lucide-react';
import { useAuth } from '@/lib/auth';
import type { Book } from '@/lib/types';

interface ProfilePageProps {
  books: Book[];
}

export function ProfilePage({ books }: ProfilePageProps) {
  const { user, signOut } = useAuth();
  const [signingOut, setSigningOut] = useState(false);

  const completed = books.length;
  const rated = books.filter((b) => b.rating > 0);
  const avgRating = rated.length
    ? rated.reduce((sum, b) => sum + b.rating, 0) / rated.length
    : 0;

  const name = user?.user_metadata?.full_name ?? user?.email?.split('@')[0] ?? 'Reader';
  const email = user?.email ?? '';
  const avatar = user?.user_metadata?.avatar_url as string | undefined;

  async function handleSignOut() {
    setSigningOut(true);
    try {
      await signOut();
    } catch {
      setSigningOut(false);
    }
  }

  return (
    <div className="mx-auto w-full max-w-3xl px-4 pb-28 pt-8 sm:px-6 lg:pb-12">
      <h1 className="mb-6 font-serif text-3xl font-semibold text-cream-50 sm:text-4xl">Profile</h1>

      {/* Profile card */}
      <section className="flex flex-col items-center gap-4 rounded-3xl bg-gradient-to-br from-midnight-800/70 to-midnight-950/70 p-6 text-center shadow-card ring-1 ring-midnight-700/40 sm:flex-row sm:text-left">
        <div className="grid h-20 w-20 shrink-0 place-items-center overflow-hidden rounded-full bg-gradient-to-br from-lavender-500 to-midnight-600 ring-2 ring-lavender-400/30">
          {avatar ? (
            <img src={avatar} alt={name} className="h-full w-full object-cover" />
          ) : (
            <span className="font-serif text-2xl font-semibold text-cream-50">
              {String(name).charAt(0).toUpperCase()}
            </span>
          )}
        </div>
        <div className="flex-1">
          <h2 className="font-serif text-xl font-semibold text-cream-50">{name}</h2>
          {email && <p className="text-sm text-midnight-100/60">{email}</p>}
        </div>
      </section>

      {/* Stats */}
      <section className="mt-6 grid grid-cols-2 gap-4">
        <StatTile
          icon={<BookOpen className="h-5 w-5" />}
          value={String(completed)}
          label="Books Completed"
        />
        <StatTile
          icon={<Star className="h-5 w-5" />}
          value={avgRating > 0 ? avgRating.toFixed(1) : '—'}
          label="Average Rating"
        />
      </section>

      {/* Sign out */}
      <section className="mt-8">
        <button
          type="button"
          onClick={handleSignOut}
          disabled={signingOut}
          className="flex w-full items-center justify-center gap-2 rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm font-semibold text-red-300 transition-colors hover:bg-red-500/20 disabled:opacity-50"
        >
          {signingOut ? <Loader2 className="h-4 w-4 animate-spin" /> : <LogOut className="h-4 w-4" />}
          Sign Out
        </button>
      </section>
    </div>
  );
}

function StatTile({
  icon,
  value,
  label,
}: {
  icon: React.ReactNode;
  value: string;
  label: string;
}) {
  return (
    <div className="rounded-2xl bg-midnight-800/50 p-5 ring-1 ring-midnight-700/40">
      <div className="mb-3 grid h-10 w-10 place-items-center rounded-xl bg-midnight-900/50 text-lavender-200 ring-1 ring-midnight-700/40">
        {icon}
      </div>
      <p className="font-serif text-3xl font-semibold text-cream-50">{value}</p>
      <p className="mt-0.5 text-xs uppercase tracking-wide text-midnight-100/60">{label}</p>
    </div>
  );
}
