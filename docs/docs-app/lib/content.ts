import fs from 'fs';
import path from 'path';
import matter from 'gray-matter';
import { DocPage, NavItem, PageFrontmatter } from './types';

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
  
  return fs.readdirSync(dir)
    .filter(file => /\.mdx?$/.test(file))
    .sort((a, b) => {
      // index.md always first
      if (a === 'index.md' || a === 'index.mdx') return -1;
      if (b === 'index.md' || b === 'index.mdx') return 1;
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
    
    // Read frontmatter to get custom title if available
    let title = filenameToTitle(file);
    try {
      const content = fs.readFileSync(filePath, 'utf-8');
      const { data } = matter(content);
      if (data.title) {
        title = data.title;
      }
    } catch (error) {
      // Use default title
    }
    
    return {
      title,
      slug: slug === 'index' ? '' : slug,
      path: `/${versionId}/wiki/${slug === 'index' ? '' : slug}`,
    };
  });
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
