import Link from 'next/link';
import { getDefaultVersionId } from '@/lib/versions';

export default function NotFound() {
  const defaultVersion = getDefaultVersionId();
  
  return (
    <div style={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      background: 'var(--bg-primary)',
      padding: 'var(--space-8)'
    }}>
      <div style={{ textAlign: 'center', maxWidth: '500px' }}>
        <div style={{ fontSize: '6rem', marginBottom: 'var(--space-4)' }}>
          🔍
        </div>
        <h1 style={{ 
          fontSize: '2.5rem', 
          fontWeight: 800, 
          marginBottom: 'var(--space-4)',
          background: 'linear-gradient(135deg, var(--text-primary), var(--color-primary-light))',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
        }}>
          Page Not Found
        </h1>
        <p style={{ 
          color: 'var(--text-secondary)', 
          marginBottom: 'var(--space-8)',
          fontSize: '1.125rem'
        }}>
          The page you&apos;re looking for doesn&apos;t exist or has been moved.
        </p>
        <Link 
          href={`/${defaultVersion}/wiki`}
          className="btn btn--primary"
        >
          Go to Documentation
        </Link>
      </div>
    </div>
  );
}
