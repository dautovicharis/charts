import { SVGProps } from 'react';

type IconProps = SVGProps<SVGSVGElement>;

function withDefaults(props: IconProps): IconProps {
  return {
    className: 'docs-sidebar__icon',
    viewBox: '0 0 24 24',
    fill: 'none',
    'aria-hidden': 'true',
    ...props,
  };
}

export function OverviewIcon(props: IconProps) {
  return (
    <svg {...withDefaults(props)}>
      <path
        d="M3.8 10.2L12 3.8l8.2 6.4V19a1.2 1.2 0 0 1-1.2 1.2H5A1.2 1.2 0 0 1 3.8 19z"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M9.2 20.2v-5.1h5.6v5.1"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export function GettingStartedIcon(props: IconProps) {
  return (
    <svg {...withDefaults(props)}>
      <path
        d="M12 4.2v15.6M4.2 12h15.6"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <circle
        cx="12"
        cy="12"
        r="8.6"
        stroke="currentColor"
        strokeWidth="1.6"
      />
      <path
        d="M12 12l4.1-4.1"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <circle cx="12" cy="12" r="1.4" fill="currentColor" />
    </svg>
  );
}

export function ExamplesIcon(props: IconProps) {
  return (
    <svg {...withDefaults(props)}>
      <path
        d="M8.2 7.2L4.8 12l3.4 4.8M15.8 7.2l3.4 4.8-3.4 4.8"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M13.5 5.8L10.5 18.2"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
    </svg>
  );
}

export function ApiReferenceIcon(props: IconProps) {
  return (
    <svg {...withDefaults(props)}>
      <path
        d="M7 3.75h8.4L19 7.35v12.9H7z"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M15.4 3.75v3.6H19"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M10 11.25h6.1M10 14h6.1M10 16.75h4"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <path
        d="M4.5 7.25v11.5"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
    </svg>
  );
}

export function DemoGalleryIcon(props: IconProps) {
  return (
    <svg {...withDefaults(props)}>
      <rect
        x="3.5"
        y="5"
        width="17"
        height="14"
        rx="2.2"
        stroke="currentColor"
        strokeWidth="1.6"
      />
      <path
        d="M3.5 10.2h17"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <path
        d="M9.2 19V10.2"
        stroke="currentColor"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <circle cx="6.6" cy="7.6" r="1" fill="currentColor" />
      <path
        d="M14.2 15.6l1.9-1 1.8 1 1-1.8 1 1.8 1.9 1-1.9 1.1-1 1.8-1-1.8z"
        fill="currentColor"
      />
    </svg>
  );
}
