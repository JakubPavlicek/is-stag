import type { ComponentType } from 'react'

import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { Route } from '../index'

// Mock dependencies
vi.mock('@tanstack/react-router', () => ({
  createFileRoute: () => (config: any) => config,
}))

const mockKeycloak = {
  authenticated: false,
  tokenParsed: {
    given_name: 'John',
  },
  login: vi.fn(),
}

vi.mock('@react-keycloak/web', () => ({
  useKeycloak: () => ({
    keycloak: mockKeycloak,
  }),
}))

// Mock Feature components
vi.mock('@/components/features/home/HeroSection', () => ({
  HeroSection: () => <div data-testid="hero">Hero</div>,
}))

vi.mock('@/components/features/home/DashboardSection', () => ({
  DashboardSection: ({ userName }: { userName: string }) => (
    <div data-testid="dashboard">Dashboard for {userName}</div>
  ),
}))

vi.mock('@/components/features/home/PublicFeatures', () => ({
  PublicFeatures: () => <div data-testid="public-features">Public Features</div>,
}))

describe('IndexRoute', () => {
  const IndexComponent = (Route as any).component as ComponentType

  it('renders public view when not authenticated', () => {
    mockKeycloak.authenticated = false
    render(<IndexComponent />)

    expect(screen.getByTestId('hero')).toBeInTheDocument()
    expect(screen.getByTestId('public-features')).toBeInTheDocument()
    expect(screen.queryByTestId('dashboard')).not.toBeInTheDocument()
  })

  it('renders dashboard when authenticated', () => {
    mockKeycloak.authenticated = true
    render(<IndexComponent />)

    expect(screen.getByTestId('hero')).toBeInTheDocument()
    expect(screen.getByTestId('dashboard')).toBeInTheDocument()
    expect(screen.getByText('Dashboard for John')).toBeInTheDocument()
    expect(screen.queryByTestId('public-features')).not.toBeInTheDocument()
  })
})
