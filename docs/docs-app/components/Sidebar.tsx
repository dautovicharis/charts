'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { NavItem } from '@/lib/types';
import {
  ApiReferenceIcon,
  DemoGalleryIcon,
  ExternalLinkIcon,
  ExamplesIcon,
  GettingStartedIcon,
  OverviewIcon,
  PlaygroundIcon,
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
  const [mobileNavPath, setMobileNavPath] = useState<string | null>(null);
  const isMobileNavOpen = mobileNavPath === pathname;

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

  function closeMobileNavigation() {
    setMobileNavPath(null);
  }

  return (
    <aside className={`docs-sidebar ${isMobileNavOpen ? 'docs-sidebar--mobile-open' : ''}`}>
      <button
        type="button"
        className="docs-sidebar__mobile-toggle"
        onClick={() =>
          setMobileNavPath((currentPath) => (currentPath === pathname ? null : pathname))
        }
        aria-expanded={isMobileNavOpen}
        aria-controls="docs-sidebar-content"
      >
        <span>Menu</span>
        <svg
          width="12"
          height="12"
          viewBox="0 0 12 12"
          fill="none"
          aria-hidden="true"
          style={{ transform: isMobileNavOpen ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 0.2s' }}
        >
          <path
            d="M2 4L6 8L10 4"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      </button>

      <div id="docs-sidebar-content" className="docs-sidebar__content">
        <div className="docs-sidebar__section">
          <div className="docs-sidebar__section-title">Documentation</div>
          <nav className="docs-sidebar__nav">
            {navigation.map((item) => (
              <div key={item.path} className="docs-sidebar__item-group">
                <Link
                  href={item.path}
                  className={`docs-sidebar__link ${isActive(item) ? 'docs-sidebar__link--active' : ''}`}
                  onClick={closeMobileNavigation}
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
                        onClick={closeMobileNavigation}
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
              onClick={closeMobileNavigation}
            >
              <ApiReferenceIcon />
              API Reference
            </Link>
            <a
              href={`/demo/${versionId}/`}
              target="_blank"
              rel="noopener noreferrer"
              className="docs-sidebar__link"
              onClick={closeMobileNavigation}
            >
              <DemoGalleryIcon />
              <span className="docs-sidebar__link-label">
                Demo Gallery
                <ExternalLinkIcon className="docs-sidebar__external-icon" />
              </span>
            </a>
            <a
              href="/playground"
              target="_blank"
              rel="noopener noreferrer"
              className="docs-sidebar__link"
              onClick={closeMobileNavigation}
            >
              <PlaygroundIcon />
              <span className="docs-sidebar__link-label">
                Playground
                <ExternalLinkIcon className="docs-sidebar__external-icon" />
              </span>
            </a>
          </nav>
        </div>
      </div>
    </aside>
  );
}
