import { Home, Plus, Library, BookMarked, User } from 'lucide-react';
import type { ViewName } from '@/lib/types';
import { cn } from '@/lib/utils';

interface NavigationProps {
  current: ViewName;
  onNavigate: (view: ViewName) => void;
}

const ITEMS: { id: ViewName; label: string; icon: typeof Home }[] = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'add', label: 'Add', icon: Plus },
  { id: 'library', label: 'Library', icon: Library },
  { id: 'bookshelf', label: 'Shelf', icon: BookMarked },
  { id: 'profile', label: 'Profile', icon: User },
];

export function Navigation({ current, onNavigate }: NavigationProps) {
  return (
    <>
      {/* Desktop sidebar */}
      <nav className="fixed inset-y-0 left-0 hidden w-60 flex-col border-r border-midnight-700/40 bg-midnight-900/60 px-4 py-6 backdrop-blur-md lg:flex">
        <div className="mb-10 px-2">
          <p className="font-serif text-2xl font-semibold text-cream-50">Blue Hour</p>
          <p className="font-sans text-[0.65rem] uppercase tracking-[0.3em] text-lavender-300/70">
            Books
          </p>
        </div>
        <ul className="flex flex-col gap-1.5">
          {ITEMS.map(({ id, label, icon: Icon }) => {
            const active = current === id;
            return (
              <li key={id}>
                <button
                  type="button"
                  onClick={() => onNavigate(id)}
                  className={cn(
                    'flex w-full items-center gap-3 rounded-xl px-3.5 py-2.5 text-sm font-medium transition-all',
                    active
                      ? 'bg-gradient-to-r from-lavender-500/25 to-transparent text-cream-50 ring-1 ring-lavender-400/30'
                      : 'text-midnight-100/70 hover:bg-midnight-700/40 hover:text-cream-50'
                  )}
                >
                  <Icon className="h-[1.15rem] w-[1.15rem]" strokeWidth={1.75} />
                  {label}
                </button>
              </li>
            );
          })}
        </ul>
        <div className="mt-auto px-3 text-[0.7rem] text-midnight-200/40">
          <p>Your story, one book at a time.</p>
        </div>
      </nav>

      {/* Mobile bottom nav */}
      <nav className="fixed inset-x-0 bottom-0 z-40 border-t border-midnight-700/50 bg-midnight-900/85 px-2 pb-[env(safe-area-inset-bottom)] backdrop-blur-xl lg:hidden">
        <ul className="mx-auto flex max-w-md items-stretch justify-between">
          {ITEMS.map(({ id, label, icon: Icon }) => {
            const active = current === id;
            const isAdd = id === 'add';
            return (
              <li key={id} className="flex-1">
                <button
                  type="button"
                  onClick={() => onNavigate(id)}
                  className={cn(
                    'flex w-full flex-col items-center gap-0.5 py-2.5 text-[0.65rem] font-medium transition-colors',
                    active ? 'text-lavender-300' : 'text-midnight-100/60'
                  )}
                >
                  {isAdd ? (
                    <span
                      className={cn(
                        'grid h-9 w-9 place-items-center rounded-full transition-all',
                        active
                          ? 'bg-gradient-to-br from-lavender-500 to-midnight-600 text-cream-50 shadow-glow'
                          : 'bg-midnight-700/70 text-cream-50'
                      )}
                    >
                      <Icon className="h-5 w-5" strokeWidth={2} />
                    </span>
                  ) : (
                    <Icon className="h-5 w-5" strokeWidth={1.75} />
                  )}
                  {label}
                </button>
              </li>
            );
          })}
        </ul>
      </nav>
    </>
  );
}
