import { useEffect, useState } from 'react';
import { AuthProvider, useAuth } from '@/lib/auth';
import { fetchBooks } from '@/lib/books';
import type { Book, ViewName } from '@/lib/types';
import { Navigation } from '@/components/Navigation';
import { AddBookModal } from '@/components/AddBookModal';
import { BookDetailsModal } from '@/components/BookDetailsModal';
import { AuthPage } from '@/pages/AuthPage';
import { HomePage } from '@/pages/HomePage';
import { LibraryPage } from '@/pages/LibraryPage';
import { BookshelfPage } from '@/pages/BookshelfPage';
import { ProfilePage } from '@/pages/ProfilePage';
import { Loader2 } from 'lucide-react';

function AppShell() {
  const { user, loading } = useAuth();
  const [view, setView] = useState<ViewName>('home');
  const [books, setBooks] = useState<Book[]>([]);
  const [booksLoading, setBooksLoading] = useState(false);
  const [booksError, setBooksError] = useState<string | null>(null);

  const [addOpen, setAddOpen] = useState(false);
  const [detailBook, setDetailBook] = useState<Book | null>(null);
  const [detailOpen, setDetailOpen] = useState(false);

  // Load books when signed in
  useEffect(() => {
    if (!user) {
      setBooks([]);
      return;
    }
    let cancelled = false;
    setBooksLoading(true);
    setBooksError(null);
    fetchBooks()
      .then((data) => {
        if (!cancelled) setBooks(data);
      })
      .catch((err) => {
        if (!cancelled)
          setBooksError(err instanceof Error ? err.message : 'Could not load your library.');
      })
      .finally(() => {
        if (!cancelled) setBooksLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [user]);

  // Register service worker for PWA
  useEffect(() => {
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.register('/sw.js').catch(() => undefined);
    }
  }, []);

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-lavender-300" />
      </div>
    );
  }

  if (!user) {
    return <AuthPage />;
  }

  function openAdd() {
    setView('add');
    setAddOpen(true);
  }

  function openBook(book: Book) {
    setDetailBook(book);
    setDetailOpen(true);
  }

  function handleAdded(book: Book) {
    setBooks((prev) => [book, ...prev]);
  }

  function handleUpdated(updated: Book) {
    setBooks((prev) => prev.map((b) => (b.id === updated.id ? updated : b)));
    setDetailBook(updated);
  }

  function handleDeleted(id: string) {
    setBooks((prev) => prev.filter((b) => b.id !== id));
    setDetailBook(null);
  }

  return (
    <div className="min-h-screen">
      <Navigation current={view} onNavigate={setView} />
      <main className="lg:pl-60">
        {booksLoading && view !== 'add' ? (
          <div className="flex min-h-[60vh] items-center justify-center">
            <Loader2 className="h-7 w-7 animate-spin text-lavender-300" />
          </div>
        ) : booksError && books.length === 0 ? (
          <div className="mx-auto max-w-md px-6 pt-20 text-center">
            <p className="text-sm text-red-300">{booksError}</p>
          </div>
        ) : (
          <>
            {view === 'home' && (
              <HomePage
                books={books}
                userName={user.user_metadata?.full_name ?? user.email ?? null}
                onAddBook={openAdd}
                onNavigate={setView}
              />
            )}
            {view === 'add' && (
              <div className="mx-auto w-full max-w-2xl px-4 pb-28 pt-8 sm:px-6 lg:pb-12">
                <h1 className="mb-2 font-serif text-3xl font-semibold text-cream-50 sm:text-4xl">
                  Add a Book
                </h1>
                <p className="mb-6 text-sm text-midnight-100/60">
                  Search for a book you've finished, or add one manually.
                </p>
                <button
                  type="button"
                  onClick={() => setAddOpen(true)}
                  className="flex items-center gap-2 rounded-full bg-gradient-to-br from-lavender-500 to-midnight-600 px-6 py-3 text-sm font-semibold text-cream-50 shadow-glow transition-transform hover:scale-105"
                >
                  Open Book Search
                </button>
              </div>
            )}
            {view === 'library' && (
              <LibraryPage books={books} onOpenBook={openBook} onAddBook={openAdd} />
            )}
            {view === 'bookshelf' && (
              <BookshelfPage books={books} onOpenBook={openBook} onAddBook={openAdd} />
            )}
            {view === 'profile' && <ProfilePage books={books} />}
          </>
        )}
      </main>

      <AddBookModal
        open={addOpen}
        onClose={() => {
          setAddOpen(false);
          if (view === 'add') setView('home');
        }}
        onAdded={handleAdded}
      />
      <BookDetailsModal
        book={detailBook}
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
        onDeleted={handleDeleted}
        onUpdated={handleUpdated}
      />
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppShell />
    </AuthProvider>
  );
}
