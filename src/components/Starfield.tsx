import { useMemo } from 'react';

interface Star {
  top: string;
  left: string;
  size: number;
  delay: string;
  duration: string;
}

export function Starfield({ count = 24 }: { count?: number }) {
  const stars = useMemo<Star[]>(() => {
    const arr: Star[] = [];
    for (let i = 0; i < count; i++) {
      arr.push({
        top: `${Math.random() * 100}%`,
        left: `${Math.random() * 100}%`,
        size: Math.random() < 0.7 ? 2 : 3,
        delay: `${Math.random() * 4}s`,
        duration: `${3 + Math.random() * 3}s`,
      });
    }
    return arr;
  }, [count]);

  return (
    <div className="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden>
      {stars.map((s, i) => (
        <span
          key={i}
          className="absolute rounded-full bg-white animate-twinkle"
          style={{
            top: s.top,
            left: s.left,
            width: s.size,
            height: s.size,
            animationDelay: s.delay,
            animationDuration: s.duration,
            opacity: 0.4,
          }}
        />
      ))}
    </div>
  );
}
