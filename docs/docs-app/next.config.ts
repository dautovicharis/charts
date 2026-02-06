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
  
  // Rewrites to serve static assets
  async rewrites() {
    return [
      // Serve static API docs
      {
        source: '/static/api/:version/:path*',
        destination: '/api-static/:version/:path*',
      },
      // Serve static demo assets
      {
        source: '/static/demo/:version/:path*',
        destination: '/demo-static/:version/:path*',
      },
    ];
  },
};

export default nextConfig;
