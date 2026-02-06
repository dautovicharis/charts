'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { NavItem } from '@/lib/types';

interface SidebarProps {
  navigation: NavItem[];
  versionId: string;
}

export function Sidebar({ navigation, versionId }: SidebarProps) {
  const pathname = usePathname();

  function isActive(item: NavItem): boolean {
    // Normalize paths for comparison
    const itemPath = item.path.replace(/\/$/, '');
    const currentPath = pathname.replace(/\/$/, '');
    
    return itemPath === currentPath;
  }

  return (
    <aside className="docs-sidebar">
      <div className="docs-sidebar__section">
        <div className="docs-sidebar__section-title">Documentation</div>
        <nav className="docs-sidebar__nav">
          {navigation.map((item) => (
            <Link
              key={item.path}
              href={item.path}
              className={`docs-sidebar__link ${isActive(item) ? 'docs-sidebar__link--active' : ''}`}
            >
              {item.title}
            </Link>
          ))}
        </nav>
      </div>

      <div className="docs-sidebar__section">
        <div className="docs-sidebar__section-title">Resources</div>
        <nav className="docs-sidebar__nav">
          <Link
            href={`/${versionId}/api`}
            className={`docs-sidebar__link ${pathname.includes('/api') ? 'docs-sidebar__link--active' : ''}`}
          >
            📚 API Reference
          </Link>
          <a
            href={`/static/demo/${versionId}/index.html`}
            target="_blank"
            rel="noopener noreferrer"
            className="docs-sidebar__link"
          >
            🚀 JS Demo
          </a>
        </nav>
      </div>
    </aside>
  );
}
