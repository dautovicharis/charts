import Link from 'next/link';
import { VersionSwitcher } from './VersionSwitcher';
import { DocVersion } from '@/lib/types';

interface HeaderProps {
  versions: DocVersion[];
  currentVersion: DocVersion;
}

export function Header({ versions, currentVersion }: HeaderProps) {
  const snapshotVersion = versions.find((version) => version.id === 'snapshot');
  const showSnapshotShortcut = Boolean(snapshotVersion) && currentVersion.id !== 'snapshot';

  return (
    <header className="docs-header">
      <Link href={`/${currentVersion.id}/wiki`} className="docs-header__logo">
        <div className="docs-header__logo-icon" />
        <span>Charts</span>
      </Link>

      <nav className="docs-header__nav">
        {showSnapshotShortcut ? (
          <Link
            href="/snapshot/wiki"
            className="docs-header__snapshot"
            aria-label="Open development snapshot documentation"
          >
            <span className="docs-header__snapshot-dot" aria-hidden="true" />
            <span>Snapshot</span>
          </Link>
        ) : null}
        <a
          href="https://github.com/dautovicharis/charts"
          className="docs-header__link docs-header__link--github"
          target="_blank"
          rel="noopener noreferrer"
          aria-label="Charts GitHub repository"
        >
          <svg
            className="docs-header__github-icon"
            viewBox="0 0 16 16"
            aria-hidden="true"
            focusable="false"
          >
            <path
              fill="currentColor"
              d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.5-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82A7.57 7.57 0 0 1 8 4.4c.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.01 8.01 0 0 0 16 8c0-4.42-3.58-8-8-8Z"
            />
          </svg>
          <span>GitHub</span>
        </a>
        <VersionSwitcher 
          versions={versions} 
          currentVersion={currentVersion} 
        />
      </nav>
    </header>
  );
}
