import { Metadata } from 'next';
import { getAllVersions, getVersion } from '@/lib/versions';

interface ApiPageProps {
  params: Promise<{ version: string }>;
}

export async function generateMetadata({ params }: ApiPageProps): Promise<Metadata> {
  const { version } = await params;
  return {
    title: `API Reference | Charts ${version}`,
    description: `Complete API documentation for Charts ${version}`,
  };
}

export default async function ApiPage({ params }: ApiPageProps) {
  const { version: versionId } = await params;
  const version = getVersion(versionId);
  
  if (!version) {
    return <div>Version not found</div>;
  }

  // The API docs are served from static assets
  const apiUrl = `/static/api/${versionId}/index.html`;

  return (
    <div className="docs-content animate-fadeIn" style={{ maxWidth: 'none' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 'var(--space-4)' }}>
        <h1 style={{ margin: 0 }}>API Reference</h1>
        <a 
          href={apiUrl} 
          target="_blank" 
          rel="noopener noreferrer"
          className="btn btn--secondary"
        >
          Open in New Tab
        </a>
      </div>
      <p style={{ marginBottom: 'var(--space-6)', color: 'var(--text-secondary)' }}>
        Complete API documentation for Charts {version.label}, generated with Dokka.
      </p>

      <div 
        style={{
          background: 'var(--bg-secondary)',
          border: '1px solid var(--border-color)',
          borderRadius: 'var(--radius-lg)',
          overflow: 'hidden',
        }}
      >
        <iframe
          src={apiUrl}
          style={{
            width: '100%',
            height: 'calc(100vh - 280px)',
            minHeight: '600px',
            border: 'none',
          }}
          title={`API Documentation for Charts ${version.label}`}
        />
      </div>
    </div>
  );
}

export function generateStaticParams() {
  return getAllVersions().map((v) => ({ version: v.id }));
}
