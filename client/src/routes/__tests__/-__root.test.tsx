import type { ComponentType } from 'react'

import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { Route } from '../__root'

// Mock dependencies
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    i18n: { language: 'cs' },
  }),
}))

vi.mock('@tanstack/react-router', () => ({
  useRouter: () => ({
    invalidate: vi.fn(),
  }),
  Outlet: () => <div data-testid="outlet">Outlet Content</div>,
  createRootRouteWithContext: () => (config: any) => config,
}))

// Mock Layout components
vi.mock('@/components/layout/Header', () => ({
  Header: () => <div data-testid="header">Header</div>,
}))

vi.mock('@/components/layout/Sidebar', () => ({
  Sidebar: () => <div data-testid="sidebar">Sidebar</div>,
}))

describe('RootRoute', () => {
  // Since Route.component is the actual component function
  const RootComponent = (Route as any).component as ComponentType

  it('renders layout structure', () => {
    render(<RootComponent />)

    expect(screen.getByTestId('header')).toBeInTheDocument()
    expect(screen.getByTestId('sidebar')).toBeInTheDocument()
    expect(screen.getByTestId('outlet')).toBeInTheDocument()
  })
})
