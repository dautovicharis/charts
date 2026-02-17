import { notFound } from 'next/navigation';
import { Metadata } from 'next';
import { MarkdownRenderer } from '@/components';
import { getPage, getPageSlugs } from '@/lib/content';
import { getVersions } from '@/lib/versions';

interface WikiPageProps {
  params: Promise<{ version: string; slug?: string[] }>;
}

export async function generateMetadata({ params }: WikiPageProps): Promise<Metadata> {
  const { version, slug } = await params;
  const pageSlug = slug?.join('/') || '';
  const page = getPage(version, pageSlug);
  
  if (!page) {
    return { title: 'Page Not Found' };
  }

  return {
    title: `${page.title} | Charts ${version}`,
    description: page.frontmatter.description || `${page.title} - Charts documentation`,
  };
}

export default async function WikiPage({ params }: WikiPageProps) {
  const { version, slug } = await params;
  const pageSlug = slug?.join('/') || '';
  const page = getPage(version, pageSlug);

  if (!page) {
    notFound();
  }

  const isSnapshotExamples = version === 'snapshot' && pageSlug === 'examples';
  const articleClassName = isSnapshotExamples
    ? 'docs-content animate-fadeIn docs-page--snapshot-examples'
    : 'docs-content animate-fadeIn';

  return (
    <article className={articleClassName}>
      <MarkdownRenderer
        content={page.content}
        layoutVariant={isSnapshotExamples ? 'snapshotExamples' : 'default'}
      />
    </article>
  );
}

export function generateStaticParams() {
  const versions = getVersions();
  const params: { version: string; slug?: string[] }[] = [];

  for (const version of versions) {
    const slugs = getPageSlugs(version.id);
    
    for (const slug of slugs) {
      if (slug === '') {
        params.push({ version: version.id, slug: undefined });
      } else {
        params.push({ version: version.id, slug: [slug] });
      }
    }
  }

  return params;
}
