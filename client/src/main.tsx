import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import { ReactKeycloakProvider } from '@react-keycloak/web'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { RouterProvider, createRouter } from '@tanstack/react-router'

import { ThemeProvider } from '@/components/theme-provider'

import './i18n'
import './index.css'
import { keycloak } from './lib/auth'
// Import the generated route tree
import { routeTree } from './routeTree.gen'

const queryClient = new QueryClient()

const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ReactKeycloakProvider authClient={keycloak} initOptions={{ onLoad: 'check-sso' }}>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
          <RouterProvider router={router} />
        </ThemeProvider>
      </QueryClientProvider>
    </ReactKeycloakProvider>
  </StrictMode>,
)
