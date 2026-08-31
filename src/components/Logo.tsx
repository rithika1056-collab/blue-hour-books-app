import { Moon, BookOpen } from 'lucide-react';
import { cn } from '@/lib/utils';

export function Logo({ className, showText = true }: { className?: string; showText?: boolean }) {
  return (
    <div className={cn('flex items-center gap-2.5', className)}>
      <div className="relative grid h-10 w-10 place-items-center rounded-xl bg-gradient-to-br from-lavender-500 to-midnight-600 shadow-glow">
        <BookOpen className="h-5 w-5 text-cream-50" strokeWidth={1.75} />
        <Moon className="absolute -right-1 -top-1.5 h-4 w-4 fill-gold-400 text-gold-400 drop-shadow" />
      </div>
      {showText && (
        <div className="leading-none">
          <p className="font-serif text-xl font-semibold tracking-wide text-cream-50">
            Blue Hour
          </p>
          <p className="font-sans text-[0.7rem] uppercase tracking-[0.25em] text-lavender-300/80">
            Books
          </p>
        </div>
      )}
    </div>
  );
}
