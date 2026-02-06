import Link from 'next/link';
import { VersionSwitcher } from './VersionSwitcher';
import { DocVersion } from '@/lib/types';

interface HeaderProps {
  versions: DocVersion[];
  currentVersion: DocVersion;
}

export function Header({ versions, currentVersion }: HeaderProps) {
  return (
    <header className="docs-header">
      <Link href={`/${currentVersion.id}/wiki`} className="docs-header__logo">
        <div className="docs-header__logo-icon" />
        <span>Charts</span>
      </Link>

      <nav className="docs-header__nav">
        <Link href={`/${currentVersion.id}/wiki`} className="docs-header__link">
          Docs
        </Link>
        <Link href={`/${currentVersion.id}/api`} className="docs-header__link">
          API
        </Link>
        <Link href="/snapshot/playground" className="docs-header__link">
          Playground (Snapshot)
        </Link>
        <a 
          href="https://github.com/example/charts" 
          className="docs-header__link"
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub
        </a>
        <VersionSwitcher 
          versions={versions} 
          currentVersion={currentVersion} 
        />
      </nav>
    </header>
  );
}
