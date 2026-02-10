import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Privacy Policy | Charts',
  description: 'Privacy Policy for Charts app and demo applications.',
};

export default function PrivacyPolicyPage() {
  return (
    <main
      className="docs-content"
      style={{ maxWidth: '900px', margin: '0 auto', padding: '3rem 1.5rem' }}
    >
      <h1>Privacy Policy</h1>
      <p>
        <strong>Last updated:</strong> February 10, 2026
      </p>

      <p>
        This app is a demo for the Charts library. This page explains how the
        demo app handles data.
      </p>

      <h2>What We Collect</h2>
      <p>
        We do not collect personal information (such as name, email, phone
        number, or account data).
      </p>
      <p>
        Any chart data you use in the demo stays on your device and is used
        only to render charts.
      </p>

      <h2>Third-Party Platforms</h2>
      <p>
        App stores and platform providers (such as Google Play or APKPure) may
        collect technical data under their own privacy policies.
      </p>

      <h2>Data Sharing</h2>
      <p>We do not sell or share personal information.</p>

      <h2>Data Retention</h2>
      <p>
        We do not store personal data on our servers because we do not collect
        it.
      </p>

      <h2>Changes to This Policy</h2>
      <p>
        We may update this policy. Changes will appear on this page with a new
        "Last updated" date.
      </p>

      <h2>Contact</h2>
      <p>
        If you have questions about this Privacy Policy, contact us via{' '}
        <a
          href="https://github.com/dautovicharis/Charts/issues"
          target="_blank"
          rel="noopener noreferrer"
        >
          GitHub Issues
        </a>
        .
      </p>
    </main>
  );
}
