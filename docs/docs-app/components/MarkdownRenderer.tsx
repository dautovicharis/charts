'use client';

import React from 'react';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark } from 'react-syntax-highlighter/dist/cjs/styles/prism';
import { createHeadingSlugger } from '@/lib/anchors';

interface MarkdownRendererProps {
  content: string;
}

export function MarkdownRenderer({ content }: MarkdownRendererProps) {
  // Parse and render markdown content
  const elements = parseMarkdown(content);
  
  return (
    <div className="docs-content">
      {elements}
    </div>
  );
}

interface CodeBlockProps {
  code: string;
  language: string;
}

function CodeBlock({ code, language }: CodeBlockProps) {
  const [copied, setCopied] = React.useState(false);

  const handleCopy = async () => {
    try {
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(code);
      } else {
        const textarea = document.createElement('textarea');
        textarea.value = code;
        textarea.style.position = 'fixed';
        textarea.style.opacity = '0';
        document.body.appendChild(textarea);
        textarea.focus();
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
      }
      setCopied(true);
      window.setTimeout(() => setCopied(false), 1500);
    } catch {
      setCopied(false);
    }
  };

  return (
    <div className="code-block">
      <button
        className="code-block__copy"
        type="button"
        onClick={handleCopy}
        aria-label="Copy code"
      >
        {copied ? 'Copied' : 'Copy'}
      </button>
      <SyntaxHighlighter
        language={language === 'kotlin' ? 'kotlin' : language}
        style={oneDark}
        useInlineStyles={false}
        customStyle={{
          margin: 0,
          background: 'transparent',
          padding: 0,
        }}
        codeTagProps={{ style: { background: 'transparent' } }}
      >
        {code}
      </SyntaxHighlighter>
    </div>
  );
}

function parseMarkdown(content: string): React.ReactNode[] {
  const lines = content.split('\n');
  const elements: React.ReactNode[] = [];
  const makeSlug = createHeadingSlugger();
  let i = 0;
  let key = 0;

  while (i < lines.length) {
    const line = lines[i];

    // HTML block (starts with < and is not a self-closing single line)
    if (line.trim().startsWith('<') && !line.trim().startsWith('</')) {
      // Check if it's a block-level HTML element
      const blockMatch = line.trim().match(/^<(div|figure|table|section|article|aside|header|footer|nav|iframe|img)[\s>]/i);
      if (blockMatch) {
        const htmlLines: string[] = [];
        const tagName = blockMatch[1].toLowerCase();
        let depth = 0;
        
        // Collect all lines until the closing tag
        do {
          const currentLine = lines[i];
          // Count opening tags
          const openTags = (currentLine.match(new RegExp(`<${tagName}[\\s>]`, 'gi')) || []).length;
          // Count closing tags
          const closeTags = (currentLine.match(new RegExp(`</${tagName}>`, 'gi')) || []).length;
          // Handle self-closing img tags
          const selfClosing = tagName === 'img' && currentLine.includes('<img');
          
          depth += openTags - closeTags;
          htmlLines.push(currentLine);
          i++;
          
          // Break if self-closing or depth is 0
          if (selfClosing || (depth <= 0 && htmlLines.length > 0)) {
            break;
          }
        } while (i < lines.length && depth > 0);
        
        const htmlContent = htmlLines.join('\n');
        elements.push(
          <div 
            key={key++} 
            dangerouslySetInnerHTML={{ __html: htmlContent }}
            className="markdown-html-block"
          />
        );
        continue;
      }
    }

    // Code blocks
    if (line.startsWith('```')) {
      const lang = line.slice(3).trim() || 'text';
      const codeLines: string[] = [];
      i++;
      while (i < lines.length && !lines[i].startsWith('```')) {
        codeLines.push(lines[i]);
        i++;
      }
      i++; // Skip closing ```
      
      elements.push(<CodeBlock key={key++} code={codeLines.join('\n')} language={lang} />);
      continue;
    }

    // Headers
    if (line.startsWith('#')) {
      const match = line.match(/^(#{1,6})\s+(.+)$/);
      if (match) {
        const level = match[1].length;
        const rawText = match[2].trim().replace(/\s+#+\s*$/, '');
        const text = parseInlineMarkdown(rawText);
        const id = makeSlug(rawText);
        const HeadingTag = `h${level}` as 'h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6';
        elements.push(React.createElement(HeadingTag, { key: key++, id }, text));
        i++;
        continue;
      }
    }

    // Horizontal rule
    if (/^---+$/.test(line.trim())) {
      elements.push(<hr key={key++} />);
      i++;
      continue;
    }

    // Unordered list
    if (/^[-*]\s+/.test(line)) {
      const listItems: React.ReactNode[] = [];
      while (i < lines.length && /^[-*]\s+/.test(lines[i])) {
        const text = parseInlineMarkdown(lines[i].replace(/^[-*]\s+/, ''));
        listItems.push(<li key={listItems.length}>{text}</li>);
        i++;
      }
      elements.push(<ul key={key++}>{listItems}</ul>);
      continue;
    }

    // Ordered list
    if (/^\d+\.\s+/.test(line)) {
      const listItems: React.ReactNode[] = [];
      while (i < lines.length && /^\d+\.\s+/.test(lines[i])) {
        const text = parseInlineMarkdown(lines[i].replace(/^\d+\.\s+/, ''));
        listItems.push(<li key={listItems.length}>{text}</li>);
        i++;
      }
      elements.push(<ol key={key++}>{listItems}</ol>);
      continue;
    }

    // Empty line
    if (line.trim() === '') {
      i++;
      continue;
    }

    // Paragraph (but not if it looks like HTML)
    if (!line.trim().startsWith('<')) {
      const paragraphLines: string[] = [];
      while (
        i < lines.length && 
        lines[i].trim() !== '' && 
        !lines[i].startsWith('#') && 
        !lines[i].startsWith('```') && 
        !/^[-*]\s+/.test(lines[i]) && 
        !/^\d+\.\s+/.test(lines[i]) &&
        !lines[i].trim().startsWith('<')
      ) {
        paragraphLines.push(lines[i]);
        i++;
      }
      
      if (paragraphLines.length > 0) {
        const text = parseInlineMarkdown(paragraphLines.join(' '));
        elements.push(<p key={key++}>{text}</p>);
      }
      continue;
    }

    // Skip unhandled lines
    i++;
  }

  return elements;
}

function parseInlineMarkdown(text: string): React.ReactNode {
  const elements: React.ReactNode[] = [];
  let remaining = text;
  let key = 0;

  while (remaining.length > 0) {
    // Link with image: [![alt](src)](href)
    let match = remaining.match(/^(.+?)?\[!\[([^\]]*)\]\(([^)]+)\)\]\(([^)]+)\)(.*)$/);
    if (match) {
      if (match[1]) elements.push(parseInlineMarkdown(match[1]));
      const alt = match[2];
      const src = match[3];
      const href = match[4];
      const isExternal = href.startsWith('http');
      elements.push(
        <a
          key={key++}
          href={href}
          {...(isExternal ? { target: '_blank', rel: 'noopener noreferrer' } : {})}
        >
          <img src={src} alt={alt} style={{ maxWidth: '100%', height: 'auto' }} />
        </a>
      );
      remaining = match[5] || '';
      continue;
    }

    // Image
    match = remaining.match(/^(.+?)?!\[([^\]]*)\]\(([^)]+)\)(.*)$/);
    if (match) {
      if (match[1]) elements.push(parseInlineMarkdown(match[1]));
      elements.push(
        <img
          key={key++}
          src={match[3]}
          alt={match[2]}
          style={{ maxWidth: '100%', height: 'auto' }}
        />
      );
      remaining = match[4] || '';
      continue;
    }

    // Bold
    match = remaining.match(/^(.+?)?\*\*(.+?)\*\*(.*)$/);
    if (match) {
      if (match[1]) elements.push(parseInlineMarkdown(match[1]));
      elements.push(<strong key={key++}>{match[2]}</strong>);
      remaining = match[3] || '';
      continue;
    }

    // Inline code
    match = remaining.match(/^(.+?)?`([^`]+)`(.*)$/);
    if (match) {
      if (match[1]) elements.push(match[1]);
      elements.push(<code key={key++}>{match[2]}</code>);
      remaining = match[3] || '';
      continue;
    }

    // Link
    match = remaining.match(/^(.+?)?\[([^\]]+)\]\(([^)]+)\)(.*)$/);
    if (match) {
      if (match[1]) elements.push(parseInlineMarkdown(match[1]));
      const linkText = match[2];
      const href = match[3];
      const isExternal = href.startsWith('http');
      elements.push(
        <a 
          key={key++} 
          href={href}
          {...(isExternal ? { target: '_blank', rel: 'noopener noreferrer' } : {})}
        >
          {parseInlineMarkdown(linkText)}
        </a>
      );
      remaining = match[4] || '';
      continue;
    }

    // No more inline elements found
    elements.push(remaining);
    break;
  }

  return elements.length === 1 ? elements[0] : elements;
}
