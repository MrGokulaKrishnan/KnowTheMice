import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { Home } from './pages/Home';
import { DownloadPage } from './pages/Download';
import { FeaturesPage } from './pages/Features';
import { SecurityPage } from './pages/Security';
import { DocsPage } from './pages/Docs';
import { HelpPage } from './pages/Help';
import { AboutPage, ContactPage } from './pages/AboutAndContact';
import { PrivacyPolicyPage, TermsPage, CookiesPage, AccessibilityPage } from './pages/LegalPages';
import { PairingExpiredState, OfflineState, NotFoundState, ErrorState } from './pages/UXStates';

export const App: React.FC = () => {
  const [activePage, setActivePage] = useState<string>('home');

  // Scroll to top on page change
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, [activePage]);

  const renderContent = () => {
    switch (activePage) {
      case 'home':
        return <Home setActivePage={setActivePage} />;
      case 'download':
        return <DownloadPage />;
      case 'features':
        return <FeaturesPage />;
      case 'security':
        return <SecurityPage />;
      case 'docs':
        return <DocsPage />;
      case 'help':
        return <HelpPage />;
      case 'about':
        return <AboutPage />;
      case 'contact':
        return <ContactPage />;
      case 'privacy':
        return <PrivacyPolicyPage />;
      case 'terms':
        return <TermsPage />;
      case 'cookies':
        return <CookiesPage />;
      case 'accessibility':
        return <AccessibilityPage />;
      case 'security-policy':
        return <SecurityPage />;
      case 'state-expired':
        return <PairingExpiredState setActivePage={setActivePage} />;
      case 'state-offline':
        return <OfflineState setActivePage={setActivePage} />;
      case 'state-404':
        return <NotFoundState setActivePage={setActivePage} />;
      case 'state-500':
        return <ErrorState setActivePage={setActivePage} />;
      default:
        return <Home setActivePage={setActivePage} />;
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-background-amoled text-white subtle-grid relative selection:bg-brand-orange selection:text-white">
      {/* Ambient background glow accents */}
      <div className="fixed top-0 left-1/2 -translate-x-1/2 w-[800px] h-[400px] bg-brand-orange/10 blur-[140px] pointer-events-none -z-10 rounded-full" />
      <div className="fixed bottom-0 right-0 w-[500px] h-[300px] bg-brand-deep/10 blur-[120px] pointer-events-none -z-10 rounded-full" />

      {/* Main Navbar */}
      <Navbar activePage={activePage} setActivePage={setActivePage} />

      {/* Page Content */}
      <main className="flex-1">
        {renderContent()}
      </main>

      {/* Global Footer */}
      <Footer setActivePage={setActivePage} />
    </div>
  );
};
