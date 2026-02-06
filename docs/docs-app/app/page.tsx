import { redirect } from 'next/navigation';
import { getDefaultVersionId } from '@/lib/versions';

export default function HomePage() {
  const defaultVersion = getDefaultVersionId();
  redirect(`/${defaultVersion}/wiki`);
}
