import Link from 'next/link';
import { Metadata } from 'next';
import { redirect } from 'next/navigation';
import { getVersion, getVersions } from '@/lib/versions';

interface PlaygroundPageProps {
  params: Promise<{ version: string }>;
}

export async function generateMetadata({ params }: PlaygroundPageProps): Promise<Metadata> {
  const { version } = await params;
  return {
    title: `Playground | Charts ${version}`,
    description: `Interactive playground for Charts ${version} - try code live in your browser`,
  };
}

export default async function PlaygroundPage({ params }: PlaygroundPageProps) {
  const { version: versionId } = await params;
  const version = getVersion(versionId);
  if (!version) {
    return <div>Version not found</div>;
  }

  if (versionId !== 'snapshot') {
    redirect('/snapshot/playground');
  }

  return (
    <div className="docs-content animate-fadeIn">
      <div className="playground__unavailable">
        <div className="playground__unavailable-icon" aria-hidden="true">
          [ ]
        </div>
        <h2 style={{ margin: "0 0 var(--space-3)" }}>Playground coming soon</h2>
        <div className="playground__unavailable-text" style={{ textAlign: "center" }}>
          We are rebuilding the interactive demo for the new docs. For now, browse the guides or jump to the API reference.
        </div>
        <div
          style={{
            marginTop: "var(--space-6)",
            display: "flex",
            gap: "var(--space-3)",
            justifyContent: "center",
            flexWrap: "wrap",
          }}
        >
          <Link className="btn btn--primary" href={`/${versionId}/wiki`} style={{ color: "#ffffff" }}>
            Read docs
          </Link>
          <Link className="btn btn--secondary" href={`/${versionId}/api`}>
            View API
          </Link>
        </div>
      </div>
    </div>
  );
}

export function generateStaticParams() {
  return getVersions().map((v) => ({ version: v.id }));
}
