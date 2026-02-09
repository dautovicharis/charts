import fs from 'fs';
import path from 'path';
import matter from 'gray-matter';
import { DocPage, NavItem, PageFrontmatter } from './types';
import { createHeadingSlugger } from './anchors';

/**
 * Base path to wiki content
 */
const CONTENT_BASE = path.join(process.cwd(), '..', 'content');

/**
 * Get the wiki content directory for a version
 */
function getWikiPath(versionId: string): string {
  return path.join(CONTENT_BASE, versionId, 'wiki');
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
  
  return files.map(file => {
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
    };

    if (slug === 'examples' && markdownContent) {
      const children = extractExamplesChildren(markdownContent, pagePath);
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
      children.push({ title, slug: anchor, path: `${pagePath}#${anchor}` });
      continue;
    }

    if (!hasEndedPrimaryGroup && level === 3) {
      children.push({ title, slug: anchor, path: `${pagePath}#${anchor}` });
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

/**
 * Get all page slugs for a version (for static generation)
 */
export function getPageSlugs(versionId: string): string[] {
  const wikiPath = getWikiPath(versionId);
  const files = getMarkdownFiles(wikiPath);
  
  return files.map(file => {
    const slug = file.replace(/\.mdx?$/, '');
    return slug === 'index' ? '' : slug;
  });
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
    return null;
  }
  
  try {
    const fileContent = fs.readFileSync(actualPath, 'utf-8');
    const { data, content } = matter(fileContent);
    
    const frontmatter = data as PageFrontmatter;
    
    return {
      slug,
      title: frontmatter.title ?? filenameToTitle(filename),
      content,
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
