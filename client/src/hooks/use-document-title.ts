// src/hooks/useDocumentTitle.js
import { useEffect } from 'react';

export default function useDocumentTitle(title: string) {
  useEffect(() => {
    document.title = `${title}` || 'Local News Articles';
    return () => {
      document.title = 'Your App Name'; // Reset on unmount if needed
    };
  }, [title]);
}