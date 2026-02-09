'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { NavItem } from '@/lib/types';
import {
  ApiReferenceIcon,
  DemoGalleryIcon,
  ExamplesIcon,
  GettingStartedIcon,
  OverviewIcon,
} from '@/components/icons/SidebarIcons';

interface SidebarProps {
  navigation: NavItem[];
  versionId: string;
}

function getDocumentationIcon(slug: string) {
  switch (slug) {
    case '':
      return <OverviewIcon />;
    case 'getting-started':
      return <GettingStartedIcon />;
    case 'examples':
      return <ExamplesIcon />;
    default:
      return null;
  }
}

export function Sidebar({ navigation, versionId }: SidebarProps) {
  const pathname = usePathname();
  const [hash, setHash] = useState('');

  useEffect(() => {
    if (typeof window === 'undefined') {
      return;
    }

    const syncHash = () => setHash(window.location.hash);
    syncHash();
    window.addEventListener('hashchange', syncHash);
    return () => window.removeEventListener('hashchange', syncHash);
  }, [pathname]);

  function isActive(item: NavItem): boolean {
    // Normalize paths for comparison
    const itemPath = item.path.replace(/\/$/, '');
    const currentPath = pathname.replace(/\/$/, '');
    
    return itemPath === currentPath;
  }

  function isChildActive(item: NavItem): boolean {
    const [itemPath, itemHash] = item.path.split('#');
    const currentPath = pathname.replace(/\/$/, '');
    const normalizedPath = (itemPath ?? '').replace(/\/$/, '');

    return normalizedPath === currentPath && !!itemHash && hash === `#${itemHash}`;
  }

  return (
    <aside className="docs-sidebar">
      <div className="docs-sidebar__section">
        <div className="docs-sidebar__section-title">Documentation</div>
        <nav className="docs-sidebar__nav">
          {navigation.map((item) => (
            <div key={item.path} className="docs-sidebar__item-group">
              <Link
                href={item.path}
                className={`docs-sidebar__link ${isActive(item) ? 'docs-sidebar__link--active' : ''}`}
              >
                {getDocumentationIcon(item.slug)}
                {item.title}
              </Link>
              {item.children && item.children.length > 0 && isActive(item) && (
                <div className="docs-sidebar__subnav">
                  {item.children.map((child) => (
                    <Link
                      key={child.path}
                      href={child.path}
                      className={`docs-sidebar__sublink ${isChildActive(child) ? 'docs-sidebar__sublink--active' : ''}`}
                    >
                      {child.title}
                    </Link>
                  ))}
                </div>
              )}
            </div>
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
            <ApiReferenceIcon />
            API Reference
          </Link>
          <a
            href={`/static/demo/${versionId}/index.html`}
            target="_blank"
            rel="noopener noreferrer"
            className="docs-sidebar__link"
          >
            <DemoGalleryIcon />
            Demo Gallery
          </a>
        </nav>
      </div>
    </aside>
  );
}
