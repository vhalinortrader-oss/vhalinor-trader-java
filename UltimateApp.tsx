/**
 * VHALINOR Ultimate App v6.0 - Main Application Component
 * ========================================================
 * Componente principal da aplicação React com arquitetura avançada,
 * roteamento, gerenciamento de estado global, temas e integração
 * com serviços em tempo real.
 *
 * Architecture:
 * - React Router para navegação
 * - Context API para estado global
 * - WebSocket integration para dados em tempo real
 * - Theme Provider para temas dark/light
 * - Error Boundary para tratamento de erros
 * - Lazy loading para otimização de performance
 * - Responsive design com Tailwind CSS
 *
 * Features:
 * - Multi-page routing (Dashboard, AI Lab, Settings, Analytics)
 * - Real-time data streaming via WebSocket
 * - Theme switching (Dark/Light/System)
 * - Global state management
 * - Error handling and recovery
 * - Loading states and skeletons
 * - Keyboard shortcuts
 * - Mobile-responsive layout
 * - Accessibility (ARIA labels, keyboard nav)
 *
 * @module App
 * @author VHALINOR Team
 * @version 6.0.0
 * @since 2026-03-31
 */

import React, { Suspense, lazy, useEffect, useState, useCallback, useMemo } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

// ============================================================================
// LAZY LOADED COMPONENTS (Code Splitting)
// ============================================================================

const Dashboard = lazy(() => import('./components/Dashboard'));
const UltimateDashboard = lazy(() => import('./components/UltimateDashboard'));
const AILab = lazy(() => import('./components/AILab'));
const Analytics = lazy(() => import('./components/Analytics'));
const Settings = lazy(() => import('./components/Settings'));
const Portfolio = lazy(() => import('./components/Portfolio'));
const Backtest = lazy(() => import('./components/Backtest'));
const NeuralNetwork = lazy(() => import('./components/NeuralNetwork'));
const QuantumLab = lazy(() => import('./components/QuantumLab'));
const NotFound = lazy(() => import('./components/NotFound'));

// ============================================================================
// CONTEXTS
// ============================================================================

import { ThemeProvider, useTheme } from './contexts/ThemeContext';
import { WebSocketProvider } from './contexts/WebSocketContext';
import { AuthProvider } from './contexts/AuthContext';
import { NotificationProvider } from './contexts/NotificationContext';

// ============================================================================
// COMPONENTS
// ============================================================================

import { AppLayout } from './components/layout/AppLayout';
import { ErrorBoundary } from './components/ErrorBoundary';
import { LoadingScreen } from './components/LoadingScreen';
import { Toaster } from './components/ui/Toaster';

// ============================================================================
// HOOKS
// ============================================================================

import { useWebSocket } from './hooks/useWebSocket';
import { useAppState } from './hooks/useAppState';

// ============================================================================
// SERVICES
// ============================================================================

import { UltimateAIService } from './services/UltimateAI';
import { calculateUltimateIndicators } from './services/UltimateIndicators';

// ============================================================================
// UTILS
// ============================================================================

import { cn } from './lib/utils';

// ============================================================================
// TYPES
// ============================================================================

/**
 * Estado global da aplicação
 */
interface AppState {
  /** Usuário autenticado */
  isAuthenticated: boolean;
  /** Conexão WebSocket ativa */
  isConnected: boolean;
  /** Dados de mercado em tempo real */
  marketData: MarketData | null;
  /** Indicadores calculados */
  indicators: UltimateIndicatorsResult | null;
  /** Configurações do usuário */
  settings: UserSettings;
  /** Erros acumulados */
  errors: AppError[];
}

/**
 * Dados de mercado
 */
interface MarketData {
  symbol: string;
  price: number;
  change24h: number;
  volume24h: number;
  high24h: number;
  low24h: number;
  timestamp: number;
}

/**
 * Configurações do usuário
 */
interface UserSettings {
  theme: 'dark' | 'light' | 'system';
  autoRefresh: boolean;
  refreshInterval: number;
  notifications: boolean;
  soundAlerts: boolean;
  defaultSymbol: string;
  defaultTimeframe: string;
  language: string;
  timezone: string;
}

/**
 * Erro da aplicação
 */
interface AppError {
  id: string;
  message: string;
  stack?: string;
  timestamp: number;
  severity: 'low' | 'medium' | 'high' | 'critical';
}

// ============================================================================
// QUERY CLIENT CONFIGURATION
// ============================================================================

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      gcTime: 10 * 60 * 1000, // 10 minutes (renamed from cacheTime)
      retry: (failureCount, error) => {
        // Don't retry on 4xx errors
        if (error && typeof error === 'object' && 'status' in error) {
          const status = (error as any).status;
          if (status >= 400 && status < 500) return false;
        }
        return failureCount < 3;
      },
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      refetchOnWindowFocus: false,
      refetchOnReconnect: true,
      networkMode: 'online',
    },
    mutations: {
      retry: 2,
      networkMode: 'online',
    },
  },
});

// ============================================================================
// DEFAULT SETTINGS
// ============================================================================

const defaultSettings: UserSettings = {
  theme: 'dark',
  autoRefresh: true,
  refreshInterval: 5000,
  notifications: true,
  soundAlerts: false,
  defaultSymbol: 'BTCUSDT',
  defaultTimeframe: '1h',
  language: 'pt-BR',
  timezone: 'America/Sao_Paulo',
};

// ============================================================================
// ERROR BOUNDARY WRAPPER
// ============================================================================

class AppErrorBoundary extends React.Component<
  { children: React.ReactNode },
  { hasError: boolean; error: Error | null }
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('[App Error Boundary]', error, errorInfo);
    
    // Enhanced error logging
    const errorData = {
      error: {
        name: error.name,
        message: error.message,
        stack: error.stack,
      },
      errorInfo: {
        componentStack: errorInfo.componentStack,
      },
      userAgent: navigator.userAgent,
      url: window.location.href,
      timestamp: new Date().toISOString(),
    };
    
    // Log to error tracking service
    if (process.env.NODE_ENV === 'production') {
      // Send to error tracking service (Sentry, LogRocket, etc.)
      console.log('Error data for tracking service:', errorData);
    }
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white p-4" role="alert">
          <div className="max-w-md w-full text-center">
            <div className="text-6xl mb-4" aria-hidden="true">⚠️</div>
            <h1 className="text-2xl font-bold mb-4">Ops! Algo deu errado</h1>
            <p className="text-gray-400 mb-6">
              {this.state.error?.message || 'Ocorreu um erro inesperado na aplicação.'}
            </p>
            <div className="space-x-4">
              <button
                onClick={this.handleReset}
                className="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-gray-900"
                aria-label="Tentar recuperar do erro"
              >
                Tentar Novamente
              </button>
              <button
                onClick={() => window.location.reload()}
                className="px-6 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 focus:ring-offset-gray-900"
                aria-label="Recarregar a página"
              >
                Recarregar Página
              </button>
            </div>
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <details className="mt-6 text-left">
                <summary className="cursor-pointer text-sm text-gray-500 hover:text-gray-400">
                  Detalhes do erro (desenvolvimento)
                </summary>
                <pre className="mt-2 text-xs text-gray-600 overflow-auto max-h-32">
                  {this.state.error.stack}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

// ============================================================================
// ROUTE CONFIGURATION
// ============================================================================

interface RouteConfig {
  path: string;
  element: React.LazyExoticComponent<React.ComponentType<any>>;
  title: string;
  icon: string;
  requiresAuth?: boolean;
}

const routes: RouteConfig[] = [
  { path: '/', element: UltimateDashboard, title: 'Dashboard', icon: '📊' },
  { path: '/dashboard', element: UltimateDashboard, title: 'Dashboard', icon: '📊' },
  { path: '/ai-lab', element: AILab, title: 'AI Lab', icon: '🤖', requiresAuth: true },
  { path: '/analytics', element: Analytics, title: 'Analytics', icon: '📈' },
  { path: '/portfolio', element: Portfolio, title: 'Portfolio', icon: '💼', requiresAuth: true },
  { path: '/backtest', element: Backtest, title: 'Backtest', icon: '🔬' },
  { path: '/neural', element: NeuralNetwork, title: 'Neural Network', icon: '🧠' },
  { path: '/quantum', element: QuantumLab, title: 'Quantum Lab', icon: '⚛️' },
  { path: '/settings', element: Settings, title: 'Settings', icon: '⚙️' },
  { path: '*', element: NotFound, title: 'Not Found', icon: '❓' },
];

// ============================================================================
// APP CONTENT COMPONENT
// ============================================================================

function AppContent() {
  const location = useLocation();
  const { theme } = useTheme();
  const { isConnected, marketData, connect, disconnect } = useWebSocket();
  const [isLoading, setIsLoading] = useState(true);
  const [appState, setAppState] = useState<AppState>({
    isAuthenticated: false,
    isConnected: false,
    marketData: null,
    indicators: null,
    settings: defaultSettings,
    errors: [],
  });

  // Initialize app
  useEffect(() => {
    const init = async () => {
      // Load settings from localStorage
      const savedSettings = localStorage.getItem('vhalinor_settings');
      if (savedSettings) {
        setAppState(prev => ({
          ...prev,
          settings: { ...defaultSettings, ...JSON.parse(savedSettings) },
        }));
      }

      // Simulate loading
      await new Promise(resolve => setTimeout(resolve, 1500));
      setIsLoading(false);
    };

    init();
  }, []);

  // Auto-connect WebSocket with retry logic
  useEffect(() => {
    let retryTimeout: NodeJS.Timeout;
    
    const attemptConnection = () => {
      if (appState.settings.autoRefresh && !isConnected) {
        connect();
      }
    };
    
    attemptConnection();
    
    // Retry connection every 30 seconds if disconnected
    if (appState.settings.autoRefresh && !isConnected) {
      retryTimeout = setInterval(attemptConnection, 30000);
    }

    return () => {
      if (retryTimeout) {
        clearInterval(retryTimeout);
      }
      if (isConnected) {
        disconnect();
      }
    };
  }, [appState.settings.autoRefresh, isConnected, connect, disconnect]);

  // Update page title based on route
  useEffect(() => {
    const currentRoute = routes.find(r => r.path === location.pathname);
    if (currentRoute) {
      document.title = `${currentRoute.title} | VHALINOR Ultimate`;
    }
  }, [location]);

  // Enhanced keyboard shortcuts with accessibility
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Don't trigger shortcuts when user is typing in input fields
      const target = e.target as HTMLElement;
      if (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.contentEditable === 'true') {
        return;
      }
      
      // Ctrl/Cmd + K: Focus search
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        // Dispatch custom event for search focus
        window.dispatchEvent(new CustomEvent('vhalinor:search:focus'));
      }

      // Ctrl/Cmd + Shift + D: Toggle dark mode
      if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'D') {
        e.preventDefault();
        // Dispatch custom event for theme toggle
        window.dispatchEvent(new CustomEvent('vhalinor:theme:toggle'));
      }

      // Escape: Close modals/drawers
      if (e.key === 'Escape') {
        // Dispatch custom event for closing modals
        window.dispatchEvent(new CustomEvent('vhalinor:modal:close'));
      }
      
      // Ctrl/Cmd + /: Show keyboard shortcuts help
      if ((e.ctrlKey || e.metaKey) && e.key === '/') {
        e.preventDefault();
        // Dispatch custom event for shortcuts help
        window.dispatchEvent(new CustomEvent('vhalinor:shortcuts:help'));
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  // Handle errors with improved severity detection
  const handleError = useCallback((error: Error, errorInfo?: React.ErrorInfo) => {
    console.error('[App Error]', error, errorInfo);
    
    // Determine error severity based on message content
    const getErrorSeverity = (message: string): 'low' | 'medium' | 'high' | 'critical' => {
      const lowerMessage = message.toLowerCase();
      if (lowerMessage.includes('network') || lowerMessage.includes('connection')) {
        return 'medium';
      }
      if (lowerMessage.includes('authentication') || lowerMessage.includes('unauthorized')) {
        return 'high';
      }
      if (lowerMessage.includes('critical') || lowerMessage.includes('fatal')) {
        return 'critical';
      }
      return 'low';
    };
    
    const newError: AppError = {
      id: crypto.randomUUID(),
      message: error.message,
      stack: error.stack,
      timestamp: Date.now(),
      severity: getErrorSeverity(error.message),
    };

    setAppState(prev => ({
      ...prev,
      errors: [...prev.errors.slice(-4), newError], // Keep only last 5 errors
    }));
  }, []);

  // Save settings
  const saveSettings = useCallback((newSettings: Partial<UserSettings>) => {
    const updated = { ...appState.settings, ...newSettings };
    localStorage.setItem('vhalinor_settings', JSON.stringify(updated));
    setAppState(prev => ({ ...prev, settings: updated }));
  }, [appState.settings]);

  // Dismiss error
  const dismissError = useCallback((errorId: string) => {
    setAppState(prev => ({
      ...prev,
      errors: prev.errors.filter(e => e.id !== errorId),
    }));
  }, []);

  if (isLoading) {
    return <LoadingScreen />;
  }

  return (
    <div className={cn(
      'min-h-screen bg-background text-foreground transition-colors duration-300',
      theme === 'dark' && 'dark',
      theme === 'light' && 'light'
    )}>
      <AppLayout
        routes={routes}
        isConnected={isConnected}
        marketData={marketData}
        settings={appState.settings}
        onSettingsChange={saveSettings}
      >
        <Suspense
          fallback={
            <div className="flex items-center justify-center h-full">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
            </div>
          }
        >
          <Routes>
            {routes.map(route => (
              <Route
                key={route.path}
                path={route.path}
                element={
                  route.requiresAuth && !appState.isAuthenticated ? (
                    <Navigate to="/" replace />
                  ) : (
                    <route.element
                      marketData={marketData}
                      indicators={appState.indicators}
                      settings={appState.settings}
                      onError={handleError}
                    />
                  )
                }
              />
            ))}
          </Routes>
        </Suspense>
      </AppLayout>

      {/* Enhanced Error Toast Notifications */}
      {appState.errors.length > 0 && (
        <div className="fixed bottom-4 right-4 z-50 space-y-2" role="alert" aria-live="polite">
          {appState.errors.map(error => (
            <div
              key={error.id}
              className={cn(
                "px-4 py-3 rounded-lg shadow-lg flex items-center gap-3 transition-all duration-300",
                error.severity === 'critical' && "bg-red-700 text-white border-l-4 border-red-500",
                error.severity === 'high' && "bg-red-600 text-white border-l-4 border-red-400",
                error.severity === 'medium' && "bg-orange-600 text-white border-l-4 border-orange-400",
                error.severity === 'low' && "bg-yellow-600 text-white border-l-4 border-yellow-400"
              )}
            >
              <span className="flex-shrink-0" aria-hidden="true">
                {error.severity === 'critical' && '🔴'}
                {error.severity === 'high' && '❌'}
                {error.severity === 'medium' && '⚠️'}
                {error.severity === 'low' && 'ℹ️'}
              </span>
              <div className="flex-1">
                <span className="text-sm font-medium">{error.message}</span>
                <span className="text-xs opacity-75 block mt-1">
                  {new Date(error.timestamp).toLocaleTimeString()}
                </span>
              </div>
              <button
                onClick={() => dismissError(error.id)}
                className="ml-2 text-white/80 hover:text-white transition-colors"
                aria-label="Dismiss error"
                title="Dismiss error"
              >
                ✕
              </button>
            </div>
          ))}
        </div>
      )}

      <Toaster />
    </div>
  );
}

// ============================================================================
// MAIN APP COMPONENT
// ============================================================================

export default function UltimateApp() {
  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <AuthProvider>
            <WebSocketProvider>
              <NotificationProvider>
                <Router>
                  <AppContent />
                </Router>
              </NotificationProvider>
            </WebSocketProvider>
          </AuthProvider>
        </ThemeProvider>
        
        {/* React Query Devtools - Only in development */}
        {process.env.NODE_ENV === 'development' && (
          <ReactQueryDevtools initialIsOpen={false} position="bottom-right" />
        )}
      </QueryClientProvider>
    </ErrorBoundary>
  );
}

// ============================================================================
// LEGACY EXPORTS (for backward compatibility)
// ============================================================================

/**
 * Componente App legado
 * @deprecated Use UltimateApp
 */
export function App() {
  return <UltimateApp />;
}

// Default export
export { UltimateApp };
