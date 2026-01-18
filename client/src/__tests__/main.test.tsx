import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

// Import App after mocks are defined
import { App } from '../main'

// Mock react-dom/client to prevent actual mounting in tests
vi.mock('react-dom/client', () => ({
  createRoot: vi.fn(() => ({
    render: vi.fn(),
  })),
}))

// Mock react-i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  initReactI18next: {
    type: '3rdParty',
    init: vi.fn(),
  },
}))

// Mock @react-keycloak/web
const mockUseKeycloak = vi.fn()
vi.mock('@react-keycloak/web', () => ({
  useKeycloak: () => mockUseKeycloak(),
  ReactKeycloakProvider: ({ children }: any) => <div>{children}</div>,
}))

// Mock theme provider
vi.mock('@/components/theme/theme-provider', () => ({
  useTheme: () => ({
    theme: 'light',
  }),
  ThemeProvider: ({ children }: any) => <div>{children}</div>,
}))

// Mock @tanstack/react-router
vi.mock('@tanstack/react-router', () => ({
  RouterProvider: () => <div data-testid="router-provider" />,
  createRouter: vi.fn(),
  createRootRouteWithContext: () => (config: any) => config,
  createFileRoute: () => (config: any) => config,
}))

// Mock sonner
vi.mock('sonner', () => ({
  Toaster: () => <div data-testid="toaster" />,
}))

// Mock routeTree.gen
vi.mock('../routeTree.gen', () => ({
  routeTree: {},
}))

describe('App', () => {
  it('renders loading state when keycloak is not initialized', () => {
    mockUseKeycloak.mockReturnValue({ initialized: false })
    render(<App />)
    expect(screen.getByText('loading_app')).toBeInTheDocument()
  })

  it('renders provider structure when keycloak is initialized', () => {
    mockUseKeycloak.mockReturnValue({ initialized: true })
    render(<App />)
    expect(screen.getByTestId('router-provider')).toBeInTheDocument()
    expect(screen.getByTestId('toaster')).toBeInTheDocument()
  })
})
