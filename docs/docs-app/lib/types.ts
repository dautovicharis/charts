/**
 * Types for the Charts Documentation V2 system
 */

/**
 * Represents a documentation version from the registry
 */
export interface DocVersion {
  id: string;
  label: string;
  visible?: boolean;
  wikiRoot: string;
  apiBase: string;
  demoBase?: string;
  releasedAt: string;
  notes?: string;
}

/**
 * Version registry structure
 */
export interface VersionRegistry {
  versions: DocVersion[];
}

/**
 * Navigation item for sidebar
 */
export interface NavItem {
  title: string;
  slug: string;
  path: string;
  children?: NavItem[];
}

/**
 * Markdown page frontmatter (optional)
 */
export interface PageFrontmatter {
  title?: string;
  description?: string;
  order?: number;
}

/**
 * Parsed markdown page
 */
export interface DocPage {
  slug: string;
  title: string;
  content: string;
  frontmatter: PageFrontmatter;
}

/**
 * Sidebar section configuration
 */
export interface SidebarSection {
  title: string;
  items: NavItem[];
}
