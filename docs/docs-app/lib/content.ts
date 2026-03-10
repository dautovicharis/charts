import fs from 'fs';
import path from 'path';
import matter from 'gray-matter';
import { DocPage, NavItem, PageFrontmatter } from './types';
import { createHeadingSlugger } from './anchors';

/**
 * Base path to wiki content
 */
const CONTENT_BASE = path.join(process.cwd(), '..', 'content');
const SNAPSHOT_CHANGESETS_PATH = path.join(CONTENT_BASE, 'snapshot', 'changes');
const GITHUB_PULL_BASE = 'https://github.com/dautovicharis/charts/pull';

/**
 * Get the wiki content directory for a version
 */
function getWikiPath(versionId: string): string {
  return path.join(CONTENT_BASE, versionId, 'wiki');
}

function getBreakingChangesPath(versionId: string): string {
  return path.join(CONTENT_BASE, versionId, 'breaking-changes.md');
}

interface SnapshotChangeset {
  fileName: string;
  type: string;
  module: string;
  pr: string;
  summary: string;
  releaseNote: string;
  notes: string;
}

function normalizeChangesetValue(value: string): string {
  const trimmed = value.trim();
  if (!trimmed) {
    return '';
  }

  let normalized = trimmed;
  if (normalized.startsWith('`') && normalized.endsWith('`') && normalized.length >= 2) {
    normalized = normalized.slice(1, -1);
  }
  if (normalized.startsWith('<') && normalized.endsWith('>') && normalized.length >= 2) {
    normalized = '';
  }
  return normalized.trim();
}

function parseChangesetFile(filePath: string): SnapshotChangeset | null {
  try {
    const content = fs.readFileSync(filePath, 'utf-8');
    const record: Record<string, string> = {};

    for (const line of content.split('\n')) {
      const match = line.match(/^-+\s*([a-zA-Z_]+):\s*(.*)$/);
      if (!match) {
        continue;
      }
      record[match[1]] = normalizeChangesetValue(match[2]);
    }

    const fileName = path.basename(filePath);
    const summary = record.summary || '';
    const releaseNote = record.release_note || '';
    if (!summary && !releaseNote) {
      return null;
    }

    return {
      fileName,
      type: (record.type || 'other').toLowerCase(),
      module: record.module || 'unknown',
      pr: record.pr || '',
      summary,
      releaseNote,
      notes: record.notes || '',
    };
  } catch {
    return null;
  }
}

function getSnapshotChangesets(): SnapshotChangeset[] {
  if (!fs.existsSync(SNAPSHOT_CHANGESETS_PATH)) {
    return [];
  }

  return fs
    .readdirSync(SNAPSHOT_CHANGESETS_PATH)
    .filter((file) => /\.mdx?$/.test(file))
    .sort((a, b) => a.localeCompare(b))
    .map((file) => parseChangesetFile(path.join(SNAPSHOT_CHANGESETS_PATH, file)))
    .filter((item): item is SnapshotChangeset => item !== null);
}

function typeHeading(type: string): string {
  switch (type) {
    case 'feature':
      return 'Features';
    case 'fix':
      return 'Fixes';
    case 'refactor':
      return 'Refactors';
    case 'docs':
      return 'Documentation';
    case 'chore':
      return 'Chores';
    default:
      return 'Other';
  }
}

function parsePrLink(pr: string): string {
  const value = pr.trim();
  if (!value) {
    return '';
  }

  const fullUrlMatch = value.match(/^https?:\/\/github\.com\/[^/]+\/[^/]+\/pull\/(\d+)\/?$/i);
  if (fullUrlMatch) {
    const number = fullUrlMatch[1];
    return `[#${number}](${value})`;
  }

  const numberMatch = value.match(/#?(\d+)/);
  if (numberMatch) {
    const number = numberMatch[1];
    return `[#${number}](${GITHUB_PULL_BASE}/${number})`;
  }

  return '';
}

function renderSnapshotChangesetsMarkdown(changesets: SnapshotChangeset[]): string {
  if (changesets.length === 0) {
    return '';
  }

  const typeOrder = ['feature', 'fix', 'refactor', 'docs', 'chore', 'other'];
  const grouped = new Map<string, SnapshotChangeset[]>();

  for (const changeset of changesets) {
    const key = typeOrder.includes(changeset.type) ? changeset.type : 'other';
    const entries = grouped.get(key) ?? [];
    entries.push(changeset);
    grouped.set(key, entries);
  }

  const lines: string[] = [];

  for (const type of typeOrder) {
    const entries = grouped.get(type);
    if (!entries || entries.length === 0) {
      continue;
    }

    lines.push(`#### ${typeHeading(type)}`);
    lines.push('');

    for (const entry of entries) {
      const details: string[] = [];
      if (entry.module && entry.module !== 'unknown') {
        details.push(entry.module);
      }
      const prLink = parsePrLink(entry.pr);
      if (prLink) {
        details.push(prLink);
      }
      const detailSuffix = details.length > 0 ? ` (${details.join(' · ')})` : '';
      const primaryText = entry.releaseNote || entry.summary;
      lines.push(`- ${primaryText}${detailSuffix}`);
    }

    lines.push('');
  }

  return lines.join('\n').trimEnd();
}

function getVersionBreakingChangesMarkdown(versionId: string): string {
  const breakingChangesPath = getBreakingChangesPath(versionId);
  if (!fs.existsSync(breakingChangesPath)) {
    return '';
  }

  const raw = fs.readFileSync(breakingChangesPath, 'utf-8').trim();
  if (!raw) {
    return '';
  }

  const lines = raw.split('\n');
  if (/^#\s+breaking change/i.test(lines[0].trim())) {
    lines.shift();
    while (lines.length > 0 && lines[0].trim() === '') {
      lines.shift();
    }
  }

  return lines.join('\n').trim();
}

function getDefaultMigrationPageMarkdown(): string {
  return `# Migration Guide

This page summarizes breaking changes and how to migrate call sites safely. Use the module sections below to jump directly to impacted APIs.`;
}

function countMigrationGuides(markdown: string): number {
  if (!markdown.trim()) {
    return 0;
  }

  return markdown
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => /^##\s+/.test(line))
    .map((line) => line.replace(/^##\s+/, '').trim().toLowerCase())
    .filter((title) => title !== 'overview' && title !== 'breaking changes / migration')
    .length;
}

function injectIntoSection(
  content: string,
  generatedMarkdown: string,
  sectionPattern: RegExp,
  appendIfMissing: boolean,
): string {
  if (!generatedMarkdown.trim()) {
    return content;
  }

  const lines = content.split('\n');
  const sectionIndex = lines.findIndex((line) => sectionPattern.test(line.trim()));

  if (sectionIndex < 0) {
    if (!appendIfMissing) {
      return content;
    }
    return `${content.trimEnd()}\n\n${generatedMarkdown}\n`;
  }

  let insertAt = sectionIndex + 1;
  while (insertAt < lines.length && lines[insertAt].trim() === '') {
    insertAt += 1;
  }

  const before = lines.slice(0, insertAt);
  const after = lines.slice(insertAt);

  return [...before, '', generatedMarkdown, '', ...after].join('\n').trimEnd() + '\n';
}

function injectIntoWhatsNewSection(content: string, generatedMarkdown: string): string {
  return injectIntoSection(content, generatedMarkdown, /^##\s+What's New in\b/i, true);
}

function removeSection(content: string, sectionPattern: RegExp): string {
  const lines = content.split('\n');
  const sectionIndex = lines.findIndex((line) => sectionPattern.test(line.trim()));
  if (sectionIndex < 0) {
    return content;
  }

  let removeEnd = sectionIndex + 1;
  while (removeEnd < lines.length) {
    const trimmed = lines[removeEnd].trim();
    if (/^##\s+/.test(trimmed)) {
      break;
    }
    removeEnd += 1;
  }

  const updated = [...lines.slice(0, sectionIndex), ...lines.slice(removeEnd)].join('\n');
  return `${updated.trimEnd()}\n`;
}

/**
 * Convert a filename to a human-readable title
 */
function filenameToTitle(filename: string): string {
  // Remove .md extension
  const name = filename.replace(/\.mdx?$/, '');
  
  // Handle index files
  if (name === 'index') {
    return 'Overview';
  }
  
  // Convert kebab-case to Title Case
  return name
    .split('-')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}

/**
 * Get all markdown files in a directory
 */
function getMarkdownFiles(dir: string): string[] {
  if (!fs.existsSync(dir)) {
    return [];
  }
  
  const orderRank: Record<string, number> = {
    index: 0,
    'getting-started': 1,
  };

  return fs.readdirSync(dir)
    .filter(file => /\.mdx?$/.test(file))
    .sort((a, b) => {
      const aSlug = a.replace(/\.mdx?$/, '');
      const bSlug = b.replace(/\.mdx?$/, '');

      // Keep migration page last in the docs menu when present.
      if (aSlug === 'migration' && bSlug !== 'migration') {
        return 1;
      }
      if (bSlug === 'migration' && aSlug !== 'migration') {
        return -1;
      }

      const aRank = orderRank[aSlug] ?? Number.MAX_SAFE_INTEGER;
      const bRank = orderRank[bSlug] ?? Number.MAX_SAFE_INTEGER;

      if (aRank !== bRank) {
        return aRank - bRank;
      }

      return a.localeCompare(b);
    });
}

/**
 * Build navigation items from the wiki directory structure
 */
export function getNavigation(versionId: string): NavItem[] {
  const wikiPath = getWikiPath(versionId);
  const files = getMarkdownFiles(wikiPath);
  const versionBreakingChangesMarkdown = getVersionBreakingChangesMarkdown(versionId);
  const migrationGuideCount = countMigrationGuides(versionBreakingChangesMarkdown);
  const hasBreakingChanges = migrationGuideCount > 0;
  const hasMigrationFile = files.some((file) => /^migration\.mdx?$/.test(file));
  const navigationFiles = hasBreakingChanges && !hasMigrationFile ? [...files, 'migration.md'] : files;

  return navigationFiles
    .filter((file) => {
      const slug = file.replace(/\.mdx?$/, '');
      if (slug === 'migration' && !hasBreakingChanges) {
        return false;
      }
      return true;
    })
    .map((file) => {
      const slug = file.replace(/\.mdx?$/, '');
      const filePath = path.join(wikiPath, file);
      const pagePath = `/${versionId}/wiki/${slug === 'index' ? '' : slug}`;

      // Read frontmatter to get custom title if available
      let title = filenameToTitle(file);
      let markdownContent = '';
      try {
        const fileContent = fs.readFileSync(filePath, 'utf-8');
        const { data, content } = matter(fileContent);
        if (data.title) {
          title = data.title;
        }
        markdownContent = content;
      } catch {
        // Use default title
      }

      const navItem: NavItem = {
        title,
        slug: slug === 'index' ? '' : slug,
        path: pagePath,
        badgeCount:
          slug === 'migration' && hasBreakingChanges
            ? migrationGuideCount
            : undefined,
      };

      if (slug === 'examples' && markdownContent) {
        const children = extractExamplesChildren(markdownContent, pagePath);
        if (children.length > 0) {
          navItem.children = children;
        }
      }

      if (slug === 'migration' && (markdownContent || hasBreakingChanges)) {
        const baseMigrationMarkdown = markdownContent || getDefaultMigrationPageMarkdown();
        const markdownWithBreakingChanges = versionBreakingChangesMarkdown
          ? `${baseMigrationMarkdown.trimEnd()}\n\n${versionBreakingChangesMarkdown}\n`
          : baseMigrationMarkdown;
        const children = extractMigrationChildren(markdownWithBreakingChanges, pagePath);
        if (children.length > 0) {
          navItem.children = children;
        }
      }

      return navItem;
    });
}

/**
 * Build Examples submenu from headings:
 * - include the first contiguous group of level-3 headings
 * - include the first level-2 heading that appears after that group
 */
function extractExamplesChildren(content: string, pagePath: string): NavItem[] {
  const children: NavItem[] = [];
  const makeSlug = createHeadingSlugger();
  let hasStartedPrimaryGroup = false;
  let hasEndedPrimaryGroup = false;
  const isSnapshotVersion = pagePath.startsWith('/snapshot/');

  const displayTitle = (title: string) =>
    isSnapshotVersion ? title.replace(/\s+Chart$/, '') : title;

  for (const rawLine of content.split('\n')) {
    const match = rawLine.match(/^(#{1,6})\s+(.+)$/);
    if (!match) {
      continue;
    }

    const level = match[1].length;
    const title = match[2].trim().replace(/\s+#+\s*$/, '');
    const anchor = makeSlug(title);

    if (!hasStartedPrimaryGroup) {
      if (level !== 3) {
        continue;
      }
      hasStartedPrimaryGroup = true;
      children.push({ title: displayTitle(title), slug: anchor, path: `${pagePath}#${anchor}` });
      continue;
    }

    if (!hasEndedPrimaryGroup && level === 3) {
      children.push({ title: displayTitle(title), slug: anchor, path: `${pagePath}#${anchor}` });
      continue;
    }

    if (!hasEndedPrimaryGroup && level <= 2) {
      hasEndedPrimaryGroup = true;
      if (level === 2) {
        children.push({ title, slug: anchor, path: `${pagePath}#${anchor}` });
      }
      break;
    }
  }

  return children;
}

function extractMigrationChildren(content: string, pagePath: string): NavItem[] {
  const children: NavItem[] = [];
  const makeSlug = createHeadingSlugger();

  for (const rawLine of content.split('\n')) {
    const match = rawLine.match(/^(#{1,6})\s+(.+)$/);
    if (!match) {
      continue;
    }

    const level = match[1].length;
    const title = match[2].trim().replace(/\s+#+\s*$/, '');
    const anchor = makeSlug(title);

    if (level !== 2) {
      continue;
    }

    if (/^overview$/i.test(title)) {
      continue;
    }

    children.push({ title, slug: anchor, path: `${pagePath}#${anchor}` });
  }

  return children;
}

/**
 * Get all page slugs for a version (for static generation)
 */
export function getPageSlugs(versionId: string): string[] {
  const wikiPath = getWikiPath(versionId);
  const files = getMarkdownFiles(wikiPath);
  const versionBreakingChangesMarkdown = getVersionBreakingChangesMarkdown(versionId);
  const hasBreakingChanges = countMigrationGuides(versionBreakingChangesMarkdown) > 0;
  const hasMigrationFile = files.some((file) => /^migration\.mdx?$/.test(file));
  const pageFiles = hasBreakingChanges && !hasMigrationFile ? [...files, 'migration.md'] : files;
  
  return pageFiles
    .map(file => file.replace(/\.mdx?$/, ''))
    .filter((slug) => !(slug === 'migration' && !hasBreakingChanges))
    .map((slug) => (slug === 'index' ? '' : slug));
}

/**
 * Load a specific wiki page
 */
export function getPage(versionId: string, slug: string): DocPage | null {
  const wikiPath = getWikiPath(versionId);
  const filename = slug === '' ? 'index.md' : `${slug}.md`;
  const filePath = path.join(wikiPath, filename);
  
  // Try .md first, then .mdx
  let actualPath = filePath;
  if (!fs.existsSync(actualPath)) {
    actualPath = filePath.replace(/\.md$/, '.mdx');
  }
  
  if (!fs.existsSync(actualPath)) {
    if (slug === 'migration') {
      const breakingChangesMarkdown = getVersionBreakingChangesMarkdown(versionId);
      const migrationGuideCount = countMigrationGuides(breakingChangesMarkdown);
      if (migrationGuideCount > 0) {
        const pageContent = `${getDefaultMigrationPageMarkdown().trimEnd()}\n\n${breakingChangesMarkdown}\n`;
        return {
          slug,
          title: 'Migration',
          content: pageContent,
          frontmatter: {},
        };
      }
    }
    return null;
  }
  
  try {
    const fileContent = fs.readFileSync(actualPath, 'utf-8');
    const { data, content } = matter(fileContent);
    
    const frontmatter = data as PageFrontmatter;
    let pageContent = content;

    if (versionId === 'snapshot' && slug === '') {
      const changesetsMarkdown = renderSnapshotChangesetsMarkdown(getSnapshotChangesets());
      if (changesetsMarkdown) {
        pageContent = injectIntoWhatsNewSection(pageContent, changesetsMarkdown);
      }
    }

    if (slug === '') {
      const breakingChangesMarkdown = getVersionBreakingChangesMarkdown(versionId);
      const migrationGuideCount = countMigrationGuides(breakingChangesMarkdown);
      if (migrationGuideCount === 0) {
        pageContent = removeSection(pageContent, /^##\s+Breaking Changes \/ Migration\b/i);
      }
    }

    if (slug === 'migration') {
      const breakingChangesMarkdown = getVersionBreakingChangesMarkdown(versionId);
      const migrationGuideCount = countMigrationGuides(breakingChangesMarkdown);
      if (migrationGuideCount === 0) {
        return null;
      }
      if (breakingChangesMarkdown) {
        pageContent = `${pageContent.trimEnd()}\n\n${breakingChangesMarkdown}\n`;
      }
    }
    
    return {
      slug,
      title: frontmatter.title ?? filenameToTitle(filename),
      content: pageContent,
      frontmatter,
    };
  } catch (error) {
    console.error(`Failed to load page ${slug}:`, error);
    return null;
  }
}

/**
 * Get all pages for a version
 */
export function getAllPages(versionId: string): DocPage[] {
  const slugs = getPageSlugs(versionId);
  const pages: DocPage[] = [];
  
  for (const slug of slugs) {
    const page = getPage(versionId, slug);
    if (page) {
      pages.push(page);
    }
  }
  
  return pages;
}
