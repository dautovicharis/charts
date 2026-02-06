'use client';

import { useState, useRef, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { DocVersion } from '@/lib/types';

interface VersionSwitcherProps {
  versions: DocVersion[];
  currentVersion: DocVersion;
}

export function VersionSwitcher({ versions, currentVersion }: VersionSwitcherProps) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const pathname = usePathname();

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Replace version in current path
  function getVersionPath(version: DocVersion): string {
    // Extract the path after the version ID
    const pathParts = pathname.split('/');
    const currentVersionIndex = pathParts.findIndex(p => p === currentVersion.id);
    
    if (currentVersionIndex !== -1) {
      pathParts[currentVersionIndex] = version.id;
      return pathParts.join('/');
    }
    
    // Default to wiki root for the version
    return `/${version.id}/wiki`;
  }

  return (
    <div className="version-switcher" ref={dropdownRef}>
      <button
        className="version-switcher__button"
        onClick={() => setIsOpen(!isOpen)}
        aria-expanded={isOpen}
        aria-haspopup="listbox"
      >
        <span>{currentVersion.label}</span>
        <svg
          width="12"
          height="12"
          viewBox="0 0 12 12"
          fill="none"
          style={{ transform: isOpen ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 0.2s' }}
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

      {isOpen && (
        <div className="version-switcher__dropdown" role="listbox">
          {versions.map((version) => (
            <Link
              key={version.id}
              href={getVersionPath(version)}
              className={`version-switcher__item ${version.id === currentVersion.id ? 'version-switcher__item--current' : ''}`}
              onClick={() => setIsOpen(false)}
              role="option"
              aria-selected={version.id === currentVersion.id}
            >
              <span className="version-switcher__version">{version.label}</span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
