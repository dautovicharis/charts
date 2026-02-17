import fs from 'fs';
import path from 'path';
import { VersionRegistry, DocVersion } from './types';

/**
 * Path to the version registry file
 */
const REGISTRY_PATH = path.join(process.cwd(), '..', 'registry', 'versions.json');

/**
 * Cache for the version registry
 */
let registryCache: VersionRegistry | null = null;

function isVisible(version: DocVersion): boolean {
  return version.visible !== false;
}

/**
 * Load the version registry from disk
 */
export function getVersionRegistry(): VersionRegistry {
  if (registryCache) {
    return registryCache;
  }
  
  try {
    const content = fs.readFileSync(REGISTRY_PATH, 'utf-8');
    registryCache = JSON.parse(content) as VersionRegistry;
    return registryCache;
  } catch (error) {
    console.error('Failed to load version registry:', error);
    // Return empty registry as fallback
    return { versions: [] };
  }
}

/**
 * Get all available versions
 */
export function getAllVersions(): DocVersion[] {
  return getVersionRegistry().versions;
}

/**
 * Get all visible versions (shown in UI controls)
 */
export function getVersions(): DocVersion[] {
  return getAllVersions().filter(isVisible);
}

/**
 * Get a specific version by ID (including hidden versions)
 */
export function getVersion(versionId: string): DocVersion | undefined {
  return getAllVersions().find(v => v.id === versionId);
}

/**
 * Get the current (default) version
 */
export function getCurrentVersion(): DocVersion | undefined {
  const versions = getVersions();
  return versions.find(v => v.id !== 'snapshot') ?? versions[0];
}

/**
 * Get the default version ID
 */
export function getDefaultVersionId(): string {
  const current = getCurrentVersion();
  return current?.id ?? getAllVersions()[0]?.id ?? 'v2';
}

/**
 * Check if a version has playground enabled
 */
/**
 * Clear the registry cache (useful for development)
 */
export function clearRegistryCache(): void {
  registryCache = null;
}
