import { useState } from 'react';
import { BookOpen, Trash2, Pencil, Loader2, AlertCircle } from 'lucide-react';
import { Modal } from './Modal';
import { StarRating } from './StarRating';
import { formatDate } from './BookCard';
import { deleteBook, updateBook } from '@/lib/books';
import type { Book } from '@/lib/types';

interface BookDetailsModalProps {
  book: Book | null;
  open: boolean;
  onClose: () => void;
  onDeleted: (id: string) => void;
  onUpdated: (book: Book) => void;
}

export function BookDetailsModal({
  book,
  open,
  onClose,
  onDeleted,
  onUpdated,
}: BookDetailsModalProps) {
  const [editing, setEditing] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState({
    title: '',
    author: '',
    cover: '',
    isbn: '',
    publicationYear: '',
    publisher: '',
    description: '',
    rating: 0,
    dateCompleted: '',
  });

  function startEdit() {
    if (!book) return;
    setForm({
      title: book.title,
      author: book.author ?? '',
      cover: book.cover ?? '',
      isbn: book.isbn ?? '',
      publicationYear: book.publication_year ? String(book.publication_year) : '',
      publisher: book.publisher ?? '',
      description: book.description ?? '',
      rating: book.rating,
      dateCompleted: book.date_completed ?? '',
    });
    setError(null);
    setEditing(true);
  }

  async function saveEdit(e: React.FormEvent) {
    e.preventDefault();
    if (!book) return;
    setBusy(true);
    setError(null);
    try {
      const updated = await updateBook(book.id, {
        title: form.title.trim(),
        author: form.author.trim() || null,
        cover: form.cover.trim() || null,
        isbn: form.isbn.trim() || null,
        publication_year: form.publicationYear ? Number(form.publicationYear) : null,
        publisher: form.publisher.trim() || null,
        description: form.description.trim() || null,
        rating: form.rating,
        date_completed: form.dateCompleted || null,
      });
      onUpdated(updated);
      setEditing(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not save changes.');
    } finally {
      setBusy(false);
    }
  }

  async function confirmDelete() {
    if (!book) return;
    setBusy(true);
    setError(null);
    try {
      await deleteBook(book.id);
      onDeleted(book.id);
      setConfirmingDelete(false);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not delete this book.');
    } finally {
      setBusy(false);
    }
  }

  function handleClose() {
    setEditing(false);
    setConfirmingDelete(false);
    setError(null);
    onClose();
  }

  if (!book) return null;

  return (
    <Modal open={open} onClose={handleClose} className="sm:max-w-2xl">
      {editing ? (
        <form onSubmit={saveEdit} className="flex flex-col gap-4">
          <h2 className="font-serif text-xl font-semibold text-cream-50">Edit Book</h2>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <Field label="Book title *">
              <input value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required className="input" />
            </Field>
            <Field label="Author">
              <input value={form.author} onChange={(e) => setForm({ ...form, author: e.target.value })} className="input" />
            </Field>
            <Field label="Cover image URL">
              <input value={form.cover} onChange={(e) => setForm({ ...form, cover: e.target.value })} className="input" />
            </Field>
            <Field label="ISBN">
              <input value={form.isbn} onChange={(e) => setForm({ ...form, isbn: e.target.value })} className="input" />
            </Field>
            <Field label="Publication year">
              <input value={form.publicationYear} onChange={(e) => setForm({ ...form, publicationYear: e.target.value })} inputMode="numeric" className="input" />
            </Field>
            <Field label="Publisher">
              <input value={form.publisher} onChange={(e) => setForm({ ...form, publisher: e.target.value })} className="input" />
            </Field>
            <Field label="Date completed">
              <input type="date" value={form.dateCompleted} onChange={(e) => setForm({ ...form, dateCompleted: e.target.value })} className="input" />
            </Field>
            <Field label="Your rating">
              <div className="flex h-[2.6rem] items-center">
                <StarRating value={form.rating} onChange={(v) => setForm({ ...form, rating: v })} size={26} />
              </div>
            </Field>
          </div>
          <Field label="Description">
            <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} rows={3} className="input resize-none" />
          </Field>

          {error && (
            <div className="flex items-center gap-2 rounded-xl bg-red-500/10 px-3 py-2.5 text-sm text-red-300 ring-1 ring-red-500/20">
              <AlertCircle className="h-4 w-4 shrink-0" />
              {error}
            </div>
          )}

          <div className="flex gap-3 pt-1">
            <button type="button" onClick={() => setEditing(false)} className="flex-1 rounded-xl border border-midnight-600/50 px-4 py-2.5 text-sm font-medium text-midnight-100/80 transition-colors hover:bg-midnight-700/40">
              Cancel
            </button>
            <button type="submit" disabled={busy} className="flex flex-[1.5] items-center justify-center gap-2 rounded-xl bg-gradient-to-br from-lavender-500 to-midnight-600 px-4 py-2.5 text-sm font-semibold text-cream-50 shadow-glow transition-opacity disabled:opacity-50">
              {busy ? <Loader2 className="h-4 w-4 animate-spin" /> : <Pencil className="h-4 w-4" />}
              Save Changes
            </button>
          </div>
        </form>
      ) : confirmingDelete ? (
        <div className="flex flex-col items-center gap-4 py-4 text-center">
          <div className="grid h-14 w-14 place-items-center rounded-full bg-red-500/15 ring-1 ring-red-500/30">
            <Trash2 className="h-7 w-7 text-red-300" />
          </div>
          <div>
            <h2 className="font-serif text-xl font-semibold text-cream-50">Delete this book?</h2>
            <p className="mt-1 text-sm text-midnight-100/60">
              “{book.title}” will be removed from your library. This can't be undone.
            </p>
          </div>
          {error && (
            <div className="flex items-center gap-2 rounded-xl bg-red-500/10 px-3 py-2.5 text-sm text-red-300 ring-1 ring-red-500/20">
              <AlertCircle className="h-4 w-4 shrink-0" />
              {error}
            </div>
          )}
          <div className="flex w-full gap-3 pt-2">
            <button type="button" onClick={() => setConfirmingDelete(false)} disabled={busy} className="flex-1 rounded-xl border border-midnight-600/50 px-4 py-2.5 text-sm font-medium text-midnight-100/80 transition-colors hover:bg-midnight-700/40">
              Keep Book
            </button>
            <button type="button" onClick={confirmDelete} disabled={busy} className="flex flex-[1.3] items-center justify-center gap-2 rounded-xl bg-red-500/80 px-4 py-2.5 text-sm font-semibold text-cream-50 transition-opacity hover:bg-red-500 disabled:opacity-50">
              {busy ? <Loader2 className="h-4 w-4 animate-spin" /> : <Trash2 className="h-4 w-4" />}
              Delete
            </button>
          </div>
        </div>
      ) : (
        <div className="flex flex-col gap-5 sm:flex-row">
          <div className="mx-auto w-40 shrink-0 sm:mx-0 sm:w-44">
            <div className="aspect-[2/3] overflow-hidden rounded-xl bg-midnight-900/60 shadow-card">
              {book.cover ? (
                <img src={book.cover} alt={book.title} className="h-full w-full object-cover" />
              ) : (
                <div className="flex h-full flex-col items-center justify-center gap-2 bg-gradient-to-br from-midnight-700 to-midnight-900 p-4 text-center">
                  <BookOpen className="h-8 w-8 text-lavender-300/50" strokeWidth={1.5} />
                  <p className="font-serif text-sm text-cream-100/70">{book.title}</p>
                </div>
              )}
            </div>
          </div>
          <div className="flex min-w-0 flex-1 flex-col gap-3">
            <div>
              <h2 className="font-serif text-2xl font-semibold leading-tight text-cream-50">{book.title}</h2>
              {book.author && <p className="mt-0.5 text-sm text-lavender-200/80">by {book.author}</p>}
            </div>

            <div className="flex items-center gap-3">
              <StarRating value={book.rating} readOnly size={20} />
              <span className="text-sm text-midnight-100/60">{book.rating > 0 ? `${book.rating} / 5` : 'Not rated'}</span>
            </div>

            <dl className="grid grid-cols-2 gap-x-4 gap-y-2 text-sm">
              <Detail label="Completed" value={formatDate(book.date_completed)} />
              <Detail label="Published" value={book.publication_year ? String(book.publication_year) : '—'} />
              <Detail label="Publisher" value={book.publisher ?? '—'} />
              <Detail label="ISBN" value={book.isbn ?? '—'} />
              <Detail label="Source" value={sourceLabel(book.catalog_source)} />
            </dl>

            {book.categories && book.categories.length > 0 && (
              <div className="flex flex-wrap gap-1.5">
                {book.categories.slice(0, 8).map((category) => (
                  <span
                    key={category}
                    className="rounded-full bg-lavender-500/15 px-2.5 py-1 text-[0.7rem] text-lavender-200 ring-1 ring-lavender-400/20"
                  >
                    {category}
                  </span>
                ))}
              </div>
            )}

            {book.description && (
              <p className="text-sm leading-relaxed text-midnight-100/70">{book.description}</p>
            )}

            <div className="mt-2 flex gap-2.5">
              <button type="button" onClick={startEdit} className="flex items-center gap-1.5 rounded-xl border border-midnight-600/50 px-3.5 py-2 text-sm font-medium text-cream-50 transition-colors hover:bg-midnight-700/50">
                <Pencil className="h-4 w-4" /> Edit
              </button>
              <button type="button" onClick={() => setConfirmingDelete(true)} className="flex items-center gap-1.5 rounded-xl border border-red-500/30 px-3.5 py-2 text-sm font-medium text-red-300 transition-colors hover:bg-red-500/15">
                <Trash2 className="h-4 w-4" /> Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </Modal>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="flex flex-col gap-1.5">
      <span className="text-xs font-medium text-midnight-100/70">{label}</span>
      {children}
    </label>
  );
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-[0.7rem] uppercase tracking-wide text-midnight-200/40">{label}</dt>
      <dd className="text-cream-50/90">{value}</dd>
    </div>
  );
}

function sourceLabel(source: string | null): string {
  switch (source) {
    case 'google_books':
      return 'Google Books';
    case 'open_library':
      return 'Open Library';
    case 'manual':
      return 'Manual entry';
    default:
      return '—';
  }
}
