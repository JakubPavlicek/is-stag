import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { useTranslation } from 'react-i18next'

import { ReactKeycloakProvider, useKeycloak } from '@react-keycloak/web'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider, createRouter } from '@tanstack/react-router'
import { Loader2 } from 'lucide-react'
import { Toaster } from 'sonner'

import { ThemeProvider, useTheme } from '@/components/theme-provider'

import './i18n'
import './index.css'
import { keycloak } from './lib/auth'
import { routeTree } from './routeTree.gen'

const queryClient = new QueryClient()

const router = createRouter({
  routeTree,
  context: {
    queryClient,
  },
})

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

/**
 * Main App Component.
 * - Handles Keycloak initialization state.
 * - Shows a loader while checking authentication status.
 * - Sets up QueryClient, Router, and Toast notifications.
 */
export function App() {
  const { initialized } = useKeycloak()
  const { t } = useTranslation()
  const { theme } = useTheme()

  // Wait for Keycloak to finish initialization (checking auth status)
  if (!initialized) {
    return (
      <div className="bg-background flex h-screen flex-col items-center justify-center gap-2">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
        <p className="text-muted-foreground text-sm">{t('loading_app')}</p>
      </div>
    )
  }

  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
      <Toaster theme={theme} richColors closeButton position="top-right" />
    </QueryClientProvider>
  )
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ReactKeycloakProvider authClient={keycloak} initOptions={{ onLoad: 'check-sso' }}>
      <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
        <App />
      </ThemeProvider>
    </ReactKeycloakProvider>
  </StrictMode>,
)
