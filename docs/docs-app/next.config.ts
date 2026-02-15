import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Enable trailing slashes for clean URLs
  trailingSlash: false,
  
  // Image optimization settings
  images: {
    unoptimized: true,
  },
  
  // Turbopack configuration (Next.js 16+ uses Turbopack by default)
  turbopack: {},

  // Snapshot artifacts change frequently and keep stable filenames.
  // Disable browser caching to avoid stale playground bundles after regeneration.
  async headers() {
    return [
      {
        source: "/static/playground/snapshot/:path*",
        headers: [
          {
            key: "Cache-Control",
            value: "no-store, max-age=0",
          },
        ],
      },
    ];
  },
  
  // Redirects for convenience
  async redirects() {
    return [
      // Redirect /docs to current version
      {
        source: '/docs',
        destination: '/v2/wiki',
        permanent: false,
      },
      // Redirect /wiki to current version wiki
      {
        source: '/wiki',
        destination: '/v2/wiki',
        permanent: false,
      },
    ];
  },
};

export default nextConfig;
