import { redirect } from 'next/navigation';

interface VersionPageProps {
  params: Promise<{ version: string }>;
}

export default async function VersionPage({ params }: VersionPageProps) {
  const { version } = await params;
  redirect(`/${version}/wiki`);
}
