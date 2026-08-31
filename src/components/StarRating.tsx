import { useState } from 'react';
import { Star } from 'lucide-react';
import { cn } from '@/lib/utils';

interface StarRatingProps {
  value: number;
  onChange?: (value: number) => void;
  size?: number;
  readOnly?: boolean;
  className?: string;
}

const STEPS = [0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5];

export function StarRating({
  value,
  onChange,
  size = 24,
  readOnly = false,
  className,
}: StarRatingProps) {
  const [hover, setHover] = useState<number | null>(null);
  const display = hover ?? value;

  return (
    <div
      className={cn('flex items-center gap-0.5', readOnly && 'pointer-events-none', className)}
      role={readOnly ? 'img' : 'slider'}
      aria-label={`Rating: ${value} out of 5`}
      aria-valuenow={value}
      aria-valuemin={0}
      aria-valuemax={5}
    >
      {[1, 2, 3, 4, 5].map((star) => {
        const filled = display >= star;
        const half = !filled && display >= star - 0.5;
        return (
          <button
            key={star}
            type="button"
            disabled={readOnly}
            className="group relative p-0.5 transition-transform hover:scale-110 disabled:hover:scale-100"
            onClick={() => {
              if (!onChange) return;
              const next = value === star ? star - 0.5 : star;
              onChange(STEPS.includes(next) ? next : star);
            }}
            onMouseEnter={() => !readOnly && setHover(star)}
            onMouseLeave={() => !readOnly && setHover(null)}
          >
            <Star
              size={size}
              className={cn(
                'transition-colors',
                filled ? 'fill-gold-400 text-gold-400' : half ? 'fill-gold-400/50 text-gold-400' : 'fill-transparent text-midnight-300/60'
              )}
              strokeWidth={1.5}
            />
            {!readOnly && (
              <span className="absolute inset-y-0 left-0 w-1/2" onClick={(e) => {
                e.stopPropagation();
                onChange?.(star - 0.5);
              }} />
            )}
          </button>
        );
      })}
    </div>
  );
}
