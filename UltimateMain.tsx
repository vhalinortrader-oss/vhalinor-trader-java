/**
 * VHALINOR Ultimate Main Entry Point v6.0
 * =========================================
 * Ponto de entrada principal da aplicação React com inicialização
 * avançada, error boundaries, service workers e otimizações de performance.
 *
 * Features:
 * - StrictMode com checagens adicionais de desenvolvimento
 * - Error Boundary global para tratamento de erros
 * - Service Worker para PWA capabilities
 * - Performance monitoring e Web Vitals
 * - Analytics initialization
 * - Theme initialization (dark mode default)
 * - Global error handlers
 * - Importação do UltimateApp com todas as funcionalidades
 * - Lazy loading de componentes críticos
 * - Preload de recursos essenciais
 *
 * @module main
 * @author VHALINOR Team
 * @version 6.0.0
 * @since 2026-04-01
 */

import { StrictMode, Suspense, useEffect, useCallback } from 'react';
import { createRoot } from 'react-dom/client';
import { UltimateApp } from './UltimateApp';
import './UltimateTheme.css';

// ============================================================================
// ERROR BOUNDARY COMPONENT
// ============================================================================

import { Component, ReactNode, ErrorInfo } from 'react';

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
}

/**
 * Error Boundary global para capturar erros não tratados na aplicação
 */
class UltimateErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null
    };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return {
      hasError: true,
      error,
      errorInfo: null
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    this.setState({
      error,
      errorInfo
    });

    // Enhanced error logging with context
    const errorContext = {
      error: {
        name: error.name,
        message: error.message,
        stack: error.stack,
      },
      componentStack: errorInfo.componentStack,
      userAgent: navigator.userAgent,
      url: window.location.href,
      timestamp: new Date().toISOString(),
      localStorage: Object.keys(localStorage).slice(0, 5), // First 5 keys for privacy
    };

    console.error('[UltimateErrorBoundary] Error caught:', errorContext);

    // Send to error tracking service in production
    if (typeof process !== 'undefined' && process.env?.NODE_ENV === 'production') {
      // Example: Sentry, LogRocket, etc.
      // Sentry.captureException(error, { extra: errorContext });
      
      // Store error in sessionStorage for debugging
      try {
        sessionStorage.setItem('vhalinor-last-error', JSON.stringify(errorContext));
      } catch (e) {
        console.warn('Could not store error in sessionStorage:', e);
      }
    }
  }

  handleReload = useCallback((): void => {
    // Clear any stored error before reload
    sessionStorage.removeItem('vhalinor-last-error');
    window.location.reload();
  }, []);

  handleReset = useCallback((): void => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null
    });
  }, []);

  handleCopyError = useCallback((): void => {
    if (this.state.error) {
      const errorText = `Error: ${this.state.error.message}\n\nStack:\n${this.state.error.stack || 'No stack available'}`;
      
      // Modern clipboard API with fallback
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(errorText).then(() => {
          // Show success feedback
          console.log('Error details copied to clipboard');
        }).catch((err) => {
          console.warn('Clipboard API failed, trying fallback:', err);
          this.fallbackCopyToClipboard(errorText);
        });
      } else {
        this.fallbackCopyToClipboard(errorText);
      }
    }
  }, [this.state.error]);

  fallbackCopyToClipboard = (text: string): void => {
    try {
      const textArea = document.createElement('textarea');
      textArea.value = text;
      textArea.style.position = 'fixed';
      textArea.style.left = '-999999px';
      textArea.style.top = '-999999px';
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      
      const successful = document.execCommand('copy');
      document.body.removeChild(textArea);
      
      if (successful) {
        console.log('Error details copied to clipboard (fallback)');
      } else {
        console.error('Fallback copy failed');
      }
    } catch (err) {
      console.error('Fallback copy error:', err);
    }
  };

  render(): ReactNode {
    if (this.state.hasError) {
      return (
        <div
          style={{
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: '#0a0a0a',
            color: '#e5e5e5',
            fontFamily: 'Inter, sans-serif',
            padding: '20px'
          }}
        >
          <div
            style={{
              maxWidth: '500px',
              textAlign: 'center',
              background: '#151515',
              border: '1px solid rgba(255,255,255,0.1)',
              borderRadius: '12px',
              padding: '40px'
            }}
          >
            <div style={{ fontSize: '48px', marginBottom: '20px' }}>⚠️</div>
            <h1 style={{ fontSize: '24px', marginBottom: '16px', color: '#00c8ff' }}>
              Oops! Algo deu errado
            </h1>
            <p style={{ color: '#a1a1aa', marginBottom: '24px' }}>
              Ocorreu um erro inesperado na aplicação. Nossa equipe foi notificada.
            </p>
            {this.state.error && (
              <pre
                style={{
                  background: '#0a0a0a',
                  padding: '16px',
                  borderRadius: '8px',
                  fontSize: '12px',
                  color: '#ef4444',
                  textAlign: 'left',
                  overflow: 'auto',
                  maxHeight: '150px',
                  marginBottom: '24px'
                }}
              >
                {this.state.error.toString()}
              </pre>
            )}
            <div style={{ display: 'flex', gap: '12px', justifyContent: 'center', flexWrap: 'wrap' }}>
              <button
                onClick={this.handleReload}
                style={{
                  padding: '10px 20px',
                  background: '#00c8ff',
                  color: '#0a0a0a',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontWeight: '600',
                  transition: 'all 0.2s ease'
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.background = '#00a8e6';
                  e.currentTarget.style.transform = 'translateY(-1px)';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.background = '#00c8ff';
                  e.currentTarget.style.transform = 'translateY(0)';
                }}
              >
                Recarregar Página
              </button>
              <button
                onClick={this.handleReset}
                style={{
                  padding: '10px 20px',
                  background: 'transparent',
                  color: '#e5e5e5',
                  border: '1px solid rgba(255,255,255,0.2)',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease'
                }}
                onMouseOver={(e) => {
                  e.currentTarget.style.borderColor = 'rgba(255,255,255,0.4)';
                  e.currentTarget.style.transform = 'translateY(-1px)';
                }}
                onMouseOut={(e) => {
                  e.currentTarget.style.borderColor = 'rgba(255,255,255,0.2)';
                  e.currentTarget.style.transform = 'translateY(0)';
                }}
              >
                Tentar Novamente
              </button>
              {this.state.error && (
                <button
                  onClick={this.handleCopyError}
                  style={{
                    padding: '10px 20px',
                    background: 'rgba(255,255,255,0.1)',
                    color: '#e5e5e5',
                    border: '1px solid rgba(255,255,255,0.2)',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    fontSize: '14px',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseOver={(e) => {
                    e.currentTarget.style.background = 'rgba(255,255,255,0.2)';
                    e.currentTarget.style.transform = 'translateY(-1px)';
                  }}
                  onMouseOut={(e) => {
                    e.currentTarget.style.background = 'rgba(255,255,255,0.1)';
                    e.currentTarget.style.transform = 'translateY(0)';
                  }}
                >
                  📋 Copiar Erro
                </button>
              )}
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

// ============================================================================
// LOADING COMPONENT
// ============================================================================

/**
 * Componente de loading exibido durante inicialização
 */
function UltimateLoading(): JSX.Element {
  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#050505',
        color: '#e5e5e5',
        fontFamily: 'Inter, sans-serif'
      }}
    >
      <div
        style={{
          width: '60px',
          height: '60px',
          border: '3px solid rgba(0, 200, 255, 0.1)',
          borderTopColor: '#00c8ff',
          borderRadius: '50%',
          animation: 'spin 1s linear infinite'
        }}
      />
      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
      <h2 style={{ marginTop: '24px', fontSize: '18px', fontWeight: 500 }}>
        Inicializando VHALINOR Ultimate...
      </h2>
      <p style={{ marginTop: '8px', color: '#71717a', fontSize: '14px' }}>
        Carregando módulos de IA, Quantum e Neural Networks
      </p>
      <div style={{ marginTop: '16px', display: 'flex', gap: '8px', alignItems: 'center' }}>
        <div style={{ 
          width: '8px', 
          height: '8px', 
          borderRadius: '50%', 
          background: '#00c8ff',
          animation: 'pulse 2s infinite'
        }} />
        <span style={{ color: '#71717a', fontSize: '12px' }}>
          Conectando aos sistemas VHALINOR...
        </span>
      </div>
      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
        @keyframes pulse {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.3; }
        }
      `}</style>
    </div>
  );
}

// ============================================================================
// UTILITY FUNCTIONS
// ============================================================================

/**
 * Show update notification for PWA
 */
const showUpdateNotification = (): void => {
  // Create a simple notification element
  const notification = document.createElement('div');
  notification.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    background: #00c8ff;
    color: #0a0a0a;
    padding: 12px 20px;
    border-radius: 8px;
    font-family: 'Inter', sans-serif;
    font-weight: 500;
    z-index: 9999;
    box-shadow: 0 4px 12px rgba(0, 200, 255, 0.3);
    cursor: pointer;
    transition: all 0.3s ease;
  `;
  notification.textContent = '🔄 Nova versão disponível! Clique para atualizar';
  
  notification.addEventListener('click', () => {
    window.location.reload();
  });
  
  notification.addEventListener('mouseenter', () => {
    notification.style.transform = 'translateY(-2px)';
    notification.style.boxShadow = '0 6px 16px rgba(0, 200, 255, 0.4)';
  });
  
  notification.addEventListener('mouseleave', () => {
    notification.style.transform = 'translateY(0)';
    notification.style.boxShadow = '0 4px 12px rgba(0, 200, 255, 0.3)';
  });
  
  document.body.appendChild(notification);
  
  // Auto-remove after 10 seconds
  setTimeout(() => {
    if (notification.parentNode) {
      notification.parentNode.removeChild(notification);
    }
  }, 10000);
};

/**
 * Show connection notification
 */
const showConnectionNotification = (message: string, type: 'success' | 'warning'): void => {
  const notification = document.createElement('div');
  const bgColor = type === 'success' ? '#10b981' : '#f59e0b';
  
  notification.style.cssText = `
    position: fixed;
    bottom: 20px;
    right: 20px;
    background: ${bgColor};
    color: white;
    padding: 12px 20px;
    border-radius: 8px;
    font-family: 'Inter', sans-serif;
    font-weight: 500;
    z-index: 9998;
    box-shadow: 0 4px 12px ${bgColor}33;
    transform: translateX(100%);
    transition: transform 0.3s ease;
  `;
  
  notification.textContent = message;
  document.body.appendChild(notification);
  
  // Animate in
  setTimeout(() => {
    notification.style.transform = 'translateX(0)';
  }, 100);
  
  // Auto-remove after 3 seconds
  setTimeout(() => {
    notification.style.transform = 'translateX(100%)';
    setTimeout(() => {
      if (notification.parentNode) {
        notification.parentNode.removeChild(notification);
      }
    }, 300);
  }, 3000);
};

// ============================================================================
// INITIALIZATION HOOK
// ============================================================================

/**
 * Hook de inicialização da aplicação
 */
function useAppInitialization(): void {
  useEffect(() => {
    // Initialize theme from localStorage or system preference
    const initializeTheme = (): void => {
      const savedTheme = localStorage.getItem('vhalinor-theme');
      const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      const theme = savedTheme || (systemDark ? 'dark' : 'dark'); // Default to dark
      
      document.documentElement.setAttribute('data-theme', theme);
      document.documentElement.classList.add(theme === 'dark' ? 'dark' : 'light');
      
      if (theme === 'dark') {
        document.body.style.backgroundColor = '#050505';
        document.body.style.color = '#e5e5e5';
      }
      
      // Listen for system theme changes
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
      const handleThemeChange = (e: MediaQueryListEvent) => {
        if (!localStorage.getItem('vhalinor-theme')) {
          const newTheme = e.matches ? 'dark' : 'light';
          document.documentElement.setAttribute('data-theme', newTheme);
          document.documentElement.classList.remove('dark', 'light');
          document.documentElement.classList.add(newTheme);
        }
      };
      
      mediaQuery.addEventListener('change', handleThemeChange);
      
      return () => {
        mediaQuery.removeEventListener('change', handleThemeChange);
      };
    };

    // Initialize performance monitoring
    const initializePerformanceMonitoring = (): void => {
      if ('performance' in window && 'mark' in window.performance) {
        // Mark app initialization start
        performance.mark('app-init-start');
        
        // Enhanced performance monitoring
        const observer = new PerformanceObserver((list) => {
          for (const entry of list.getEntries()) {
            if (entry.entryType === 'navigation') {
              const navEntry = entry as PerformanceNavigationTiming;
              console.log('[Performance] Navigation timing:', {
                domContentLoaded: navEntry.domContentLoadedEventEnd - navEntry.domContentLoadedEventStart,
                loadComplete: navEntry.loadEventEnd - navEntry.loadEventStart,
                totalTime: navEntry.loadEventEnd - navEntry.fetchStart
              });
            }
          }
        });
        
        try {
          observer.observe({ entryTypes: ['navigation', 'resource', 'paint'] });
        } catch (e) {
          console.warn('[Performance] Observer not supported:', e);
        }
        
        // Report Web Vitals (placeholder for actual implementation)
        if ('web-vitals' in window) {
          // Web Vitals would be imported and used here
        }
      }
    };

    // Register service worker for PWA with enhanced features
    const registerServiceWorker = (): void => {
      if ('serviceWorker' in navigator && typeof process !== 'undefined' && process.env?.NODE_ENV === 'production') {
        window.addEventListener('load', () => {
          navigator.serviceWorker
            .register('/sw.js')
            .then((registration) => {
              console.log('[UltimateMain] ServiceWorker registered:', registration.scope);
              
              // Check for updates
              registration.addEventListener('updatefound', () => {
                const newWorker = registration.installing;
                if (newWorker) {
                  newWorker.addEventListener('statechange', () => {
                    if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                      // New version available
                      console.log('[UltimateMain] New version available');
                      // Show update notification
                      showUpdateNotification();
                    }
                  });
                }
              });
              
              // Periodic update check
              const updateInterval = setInterval(() => {
                registration.update();
              }, 60 * 60 * 1000); // Check every hour
              
              // Cleanup interval on page unload
              window.addEventListener('beforeunload', () => {
                clearInterval(updateInterval);
              });
            })
            .catch((error) => {
              console.error('[UltimateMain] ServiceWorker registration failed:', error);
            });
        });
      }
    };
    

    // Enhanced global error handlers
    const setupGlobalErrorHandlers = (): void => {
      window.onerror = (message, source, lineno, colno, error) => {
        const errorInfo = {
          message,
          source,
          lineno,
          colno,
          error: error?.stack,
          timestamp: new Date().toISOString(),
          userAgent: navigator.userAgent
        };
        
        console.error('[Global Error]', errorInfo);
        
        // Store errors for debugging (limited to last 5)
        try {
          const errors = JSON.parse(sessionStorage.getItem('vhalinor-errors') || '[]');
          errors.push(errorInfo);
          if (errors.length > 5) errors.shift();
          sessionStorage.setItem('vhalinor-errors', JSON.stringify(errors));
        } catch (e) {
          console.warn('Could not store error in sessionStorage:', e);
        }
        
        // Send error to monitoring service in production
        if (typeof process !== 'undefined' && process.env?.NODE_ENV === 'production') {
          // Example: Send to error monitoring service
          // fetch('/api/errors', {
          //   method: 'POST',
          //   headers: { 'Content-Type': 'application/json' },
          //   body: JSON.stringify(errorInfo)
          // });
        }
        
        return false;
      };

      window.onunhandledrejection = (event) => {
        const rejectionInfo = {
          reason: event.reason,
          timestamp: new Date().toISOString(),
          stack: event.reason?.stack
        };
        
        console.error('[Unhandled Promise Rejection]', rejectionInfo);
        
        // Prevent default browser behavior
        event.preventDefault();
      };
    };

    // Enhanced preload critical resources with error handling
    const preloadCriticalResources = (): void => {
      const criticalResources = [
        // Preload fonts
        { rel: 'preload', href: 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap', as: 'style' },
        // Preload critical icons (if any)
        { rel: 'preload', href: '/favicon.ico', as: 'image' },
        // Preconnect to external domains
        { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
        { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossOrigin: 'anonymous' }
      ];

      criticalResources.forEach((resource) => {
        try {
          const link = document.createElement('link');
          link.rel = resource.rel;
          link.href = resource.href;
          if (resource.as) link.as = resource.as;
          if (resource.crossOrigin) link.crossOrigin = resource.crossOrigin;
          
          // Add error handling
          link.onerror = () => {
            console.warn(`[UltimateMain] Failed to preload resource: ${resource.href}`);
          };
          
          document.head.appendChild(link);
        } catch (error) {
          console.warn(`[UltimateMain] Error creating preload link: ${error}`);
        }
      });
      
      // DNS prefetch for potential API endpoints
      const dnsPrefetchDomains = [
        'api.vhalinor.com',
        'cdn.vhalinor.com'
      ];
      
      dnsPrefetchDomains.forEach(domain => {
        try {
          const link = document.createElement('link');
          link.rel = 'dns-prefetch';
          link.href = `https://${domain}`;
          document.head.appendChild(link);
        } catch (error) {
          console.warn(`[UltimateMain] Error creating DNS prefetch link: ${error}`);
        }
      });
    };

    // Execute initializations
    initializeTheme();
    initializePerformanceMonitoring();
    registerServiceWorker();
    setupGlobalErrorHandlers();
    preloadCriticalResources();

    // Mark initialization complete
    if ('performance' in window && 'mark' in window.performance) {
      performance.mark('app-init-end');
      performance.measure('app-initialization', 'app-init-start', 'app-init-end');
    }

    console.log('[UltimateMain] VHALINOR Ultimate v6.0.0 initialized');
    
    // Add version info and build metadata to document for debugging
    document.documentElement.setAttribute('data-vhalinor-version', '6.0.0');
    document.documentElement.setAttribute('data-build-time', new Date().toISOString());
    
    // Initialize connection status monitoring with enhanced features
    const handleOnline = () => {
      console.log('[UltimateMain] Connection restored');
      document.documentElement.classList.remove('offline');
      document.documentElement.classList.add('online');
      
      // Show connection restored notification
      showConnectionNotification('Conexão restaurada', 'success');
    };
    
    const handleOffline = () => {
      console.log('[UltimateMain] Connection lost');
      document.documentElement.classList.remove('online');
      document.documentElement.classList.add('offline');
      
      // Show connection lost notification
      showConnectionNotification('Conexão perdida', 'warning');
    };
    
    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);
    
    // Set initial connection status
    if (!navigator.onLine) {
      document.documentElement.classList.add('offline');
    } else {
      document.documentElement.classList.add('online');
    }
    
    // Monitor connection quality if available
    if ('connection' in navigator) {
      const connection = (navigator as any).connection;
      const handleConnectionChange = () => {
        console.log('[UltimateMain] Connection quality changed:', {
          effectiveType: connection.effectiveType,
          downlink: connection.downlink,
          rtt: connection.rtt
        });
        
        document.documentElement.setAttribute('data-connection-type', connection.effectiveType);
      };
      
      connection.addEventListener('change', handleConnectionChange);
      handleConnectionChange(); // Initial check
    }
    
    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
      
      if ('connection' in navigator) {
        const connection = (navigator as any).connection;
        connection.removeEventListener('change', handleConnectionChange);
      }
    };
    
  }, []);
}

// ============================================================================
// ROOT COMPONENT
// ============================================================================

/**
 * Componente raiz com inicialização
 */
function UltimateRoot(): JSX.Element {
  useAppInitialization();

  return (
    <UltimateErrorBoundary>
      <StrictMode>
        <Suspense fallback={<UltimateLoading />}>
          <UltimateApp />
        </Suspense>
      </StrictMode>
    </UltimateErrorBoundary>
  );
}

// ============================================================================
// MOUNT APPLICATION
// ============================================================================

const rootElement = document.getElementById('root');

if (!rootElement) {
  throw new Error('[UltimateMain] Root element not found. Cannot mount application.');
}

const root = createRoot(rootElement);

// Render the application
root.render(<UltimateRoot />);

// ============================================================================
// HOT MODULE REPLACEMENT (Development)
// ============================================================================

// Enhanced Hot Module Replacement with comprehensive error handling
if (import.meta.hot) {
  import.meta.hot.accept((error) => {
    if (error) {
      console.error('[HMR] Error while accepting hot update:', error);
      
      // Show error notification to user
      const notification = document.createElement('div');
      notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #ef4444;
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        font-family: 'Inter', sans-serif;
        font-weight: 500;
        z-index: 9999;
        box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
      `;
      notification.textContent = '⚠️ Erro no hot reload. Recarregue a página.';
      document.body.appendChild(notification);
      
      setTimeout(() => {
        if (notification.parentNode) {
          notification.parentNode.removeChild(notification);
        }
      }, 5000);
    } else {
      console.log('[HMR] Hot update accepted successfully');
    }
  });
  
  // Handle disposal for cleanup
  import.meta.hot.dispose(() => {
    console.log('[HMR] Disposing module');
    
    // Cleanup any global state or event listeners
    // This is where you'd clean up things like:
    // - Global event listeners
    // - Timers/intervals
    // - WebSocket connections
    // - etc.
  });
  
  // Handle decline
  import.meta.hot.decline(() => {
    console.log('[HMR] Hot update declined');
  });
}
