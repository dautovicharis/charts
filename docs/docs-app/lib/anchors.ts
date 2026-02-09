/**
 * Create URL-safe anchor from a heading.
 * Mirrors markdown-style slug generation for in-page links.
 */
export function slugifyHeading(text: string): string {
  return text
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-+|-+$/g, '');
}

/**
 * Return a stable slugger that appends -1, -2, ... for duplicate headings.
 */
export function createHeadingSlugger(): (text: string) => string {
  const seen = new Map<string, number>();

  return (text: string) => {
    const base = slugifyHeading(text) || 'section';
    const count = seen.get(base) ?? 0;
    seen.set(base, count + 1);
    return count === 0 ? base : `${base}-${count}`;
  };
}
