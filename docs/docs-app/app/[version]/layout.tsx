import { notFound } from 'next/navigation';
import { Header, Sidebar } from '@/components';
import { getVersions, getVersion } from '@/lib/versions';
import { getNavigation } from '@/lib/content';

interface VersionLayoutProps {
  children: React.ReactNode;
  params: Promise<{ version: string }>;
}

export default async function VersionLayout({ children, params }: VersionLayoutProps) {
  const { version: versionId } = await params;
  const version = getVersion(versionId);
  
  if (!version) {
    notFound();
  }

  const versions = getVersions();
  const navigation = getNavigation(versionId);

  return (
    <div className="docs-layout">
      <Header versions={versions} currentVersion={version} />
      <Sidebar navigation={navigation} versionId={versionId} />
      <main className="docs-main">
        {children}
      </main>
    </div>
  );
}

export function generateStaticParams() {
  const versions = getVersions();
  return versions.map((v) => ({
    version: v.id,
  }));
}
