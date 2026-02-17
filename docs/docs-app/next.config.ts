import type { NextConfig } from "next";
import { getDefaultVersionId } from "./lib/versions";

const defaultDocsVersion = getDefaultVersionId();

const nextConfig: NextConfig = {
  // Enable trailing slashes for clean URLs
  trailingSlash: false,
  // Allow explicit trailing-slash URLs where static index files rely on relative assets.
  skipTrailingSlashRedirect: true,

  // CI materializes large docs payloads under public/. They must remain static assets,
  // but should not be traced into serverless functions.
  outputFileTracingExcludes: {
    '*': [
      './public/content/**/*',
      './public/static/**/*',
    ],
  },
  
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
        source: "/playground/snapshot/:path*",
        headers: [
          {
            key: "Cache-Control",
            value: "no-store, max-age=0",
          },
        ],
      },
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

  async rewrites() {
    return [
      {
        source: "/demo/:version/",
        destination: "/static/demo/:version/index.html",
      },
      {
        source: "/demo/:version/:path*",
        destination: "/static/demo/:version/:path*",
      },
      {
        source: "/playground/:version/",
        destination: "/static/playground/:version/index.html",
      },
      {
        source: "/playground/:version/:path*",
        destination: "/static/playground/:version/:path*",
      },
    ];
  },
  
  // Redirects for convenience
  async redirects() {
    return [
      {
        source: "/demo",
        destination: `/demo/${defaultDocsVersion}/`,
        permanent: false,
      },
      {
        source: "/playground",
        destination: "/playground/snapshot/",
        permanent: false,
      },
      {
        source: "/demo/:version/index.html",
        destination: "/demo/:version/",
        permanent: true,
      },
      {
        source: "/static/demo/:version",
        destination: "/demo/:version/",
        permanent: true,
      },
      {
        source: "/static/demo/:version/index.html",
        destination: "/demo/:version/",
        permanent: true,
      },
      {
        source: "/playground/:version/index.html",
        destination: "/playground/:version/",
        permanent: true,
      },
      {
        source: "/static/playground/:version",
        destination: "/playground/:version/",
        permanent: true,
      },
      {
        source: "/static/playground/:version/index.html",
        destination: "/playground/:version/",
        permanent: true,
      },
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
