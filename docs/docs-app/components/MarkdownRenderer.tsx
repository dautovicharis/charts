'use client';

import React from 'react';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark } from 'react-syntax-highlighter/dist/cjs/styles/prism';
import { createHeadingSlugger } from '@/lib/anchors';

interface MarkdownRendererProps {
  content: string;
  layoutVariant?: 'default' | 'snapshotExamples';
}

export function MarkdownRenderer({
  content,
  layoutVariant = 'default',
}: MarkdownRendererProps) {
  const elements = parseMarkdown(content, { layoutVariant });

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

interface ParseMarkdownOptions {
  layoutVariant: 'default' | 'snapshotExamples';
}

interface HtmlBlockParseResult {
  htmlContent: string;
  nextIndex: number;
}

interface CodeBlockParseResult {
  code: string;
  language: string;
  nextIndex: number;
}

function skipBlankLines(lines: string[], startIndex: number): number {
  let index = startIndex;
  while (index < lines.length && lines[index].trim() === '') {
    index += 1;
  }
  return index;
}

function parseHtmlBlockAt(lines: string[], startIndex: number): HtmlBlockParseResult | null {
  if (startIndex >= lines.length) {
    return null;
  }

  const firstLine = lines[startIndex].trim();
  if (!firstLine.startsWith('<') || firstLine.startsWith('</')) {
    return null;
  }

  const blockMatch = firstLine.match(
    /^<(div|figure|table|section|article|aside|header|footer|nav|iframe|img)[\s>]/i,
  );
  if (!blockMatch) {
    return null;
  }

  const tagName = blockMatch[1].toLowerCase();
  const htmlLines: string[] = [];
  let index = startIndex;
  let depth = 0;

  do {
    const currentLine = lines[index];
    const openTags = (currentLine.match(new RegExp(`<${tagName}[\\s>]`, 'gi')) || []).length;
    const closeTags = (currentLine.match(new RegExp(`</${tagName}>`, 'gi')) || []).length;
    const selfClosing = tagName === 'img' && currentLine.includes('<img');

    depth += openTags - closeTags;
    htmlLines.push(currentLine);
    index += 1;

    if (selfClosing || (depth <= 0 && htmlLines.length > 0)) {
      break;
    }
  } while (index < lines.length && depth > 0);

  return {
    htmlContent: htmlLines.join('\n'),
    nextIndex: index,
  };
}

function parseCodeBlockAt(lines: string[], startIndex: number): CodeBlockParseResult | null {
  if (startIndex >= lines.length || !lines[startIndex].startsWith('```')) {
    return null;
  }

  const language = lines[startIndex].slice(3).trim() || 'text';
  const codeLines: string[] = [];
  let index = startIndex + 1;

  while (index < lines.length && !lines[index].startsWith('```')) {
    codeLines.push(lines[index]);
    index += 1;
  }

  if (index < lines.length && lines[index].startsWith('```')) {
    index += 1;
  }

  return {
    code: codeLines.join('\n'),
    language,
    nextIndex: index,
  };
}

function parseMarkdown(content: string, options: ParseMarkdownOptions): React.ReactNode[] {
  const { layoutVariant } = options;
  const lines = content.split('\n');
  const elements: React.ReactNode[] = [];
  const makeSlug = createHeadingSlugger();
  let i = 0;
  let key = 0;

  while (i < lines.length) {
    const line = lines[i];

    if (layoutVariant === 'snapshotExamples') {
      const h3Match = line.match(/^###\s+(.+)$/);
      if (h3Match) {
        const rawText = h3Match[1].trim().replace(/\s+#+\s*$/, '');
        const id = makeSlug(rawText);
        elements.push(<h3 key={key++} id={id}>{parseInlineMarkdown(rawText)}</h3>);
        i += 1;

        let probe = skipBlankLines(lines, i);
        const htmlBlock = parseHtmlBlockAt(lines, probe);
        if (htmlBlock) {
          probe = skipBlankLines(lines, htmlBlock.nextIndex);
          const codeBlock = parseCodeBlockAt(lines, probe);
          if (codeBlock) {
            const htmlNode = (
              <div
                key={`snapshot-html-${key++}`}
                dangerouslySetInnerHTML={{ __html: htmlBlock.htmlContent }}
                className="markdown-html-block"
              />
            );
            const codeNode = (
              <CodeBlock
                key={`snapshot-code-${key++}`}
                code={codeBlock.code}
                language={codeBlock.language}
              />
            );
            elements.push(
              <div className="docs-example-split-row" key={`snapshot-row-${key++}`}>
                {htmlNode}
                {codeNode}
              </div>,
            );
            i = codeBlock.nextIndex;
          }
        }
        continue;
      }
    }

    const htmlBlock = parseHtmlBlockAt(lines, i);
    if (htmlBlock) {
      elements.push(
        <div
          key={key++}
          dangerouslySetInnerHTML={{ __html: htmlBlock.htmlContent }}
          className="markdown-html-block"
        />
      );
      i = htmlBlock.nextIndex;
      continue;
    }

    const codeBlock = parseCodeBlockAt(lines, i);
    if (codeBlock) {
      elements.push(<CodeBlock key={key++} code={codeBlock.code} language={codeBlock.language} />);
      i = codeBlock.nextIndex;
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
