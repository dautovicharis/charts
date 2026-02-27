"use client";

import { useEffect } from "react";
import Clarity from "@microsoft/clarity";

const CLARITY_PROJECT_ID = "vo4sd246x9";

declare global {
  interface Window {
    __clarityInitialized?: boolean;
  }
}

export function ClarityInit() {
  useEffect(() => {
    if (window.__clarityInitialized) {
      return;
    }

    Clarity.init(CLARITY_PROJECT_ID);
    window.__clarityInitialized = true;
  }, []);

  return null;
}
