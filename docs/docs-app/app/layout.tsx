import type { Metadata } from "next";
import { Analytics } from "@vercel/analytics/react";
import "./globals.css";

export const metadata: Metadata = {
  title: "Charts Documentation",
  description: "Comprehensive documentation for the Charts library - create beautiful, interactive charts with ease.",
  keywords: ["charts", "kotlin", "compose", "multiplatform", "visualization", "data"],
  authors: [{ name: "Charts Team" }],
  openGraph: {
    title: "Charts Documentation",
    description: "Create beautiful, interactive charts for Kotlin Multiplatform",
    type: "website",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" data-theme="dark">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link 
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap" 
          rel="stylesheet" 
        />
      </head>
      <body>
        {children}
        <Analytics />
      </body>
    </html>
  );
}
