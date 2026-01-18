import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { HeroSection } from '../HeroSection'

// Mock react-i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

// Mock react-keycloak
const mockKeycloak = {
  authenticated: false,
  login: vi.fn(),
}

vi.mock('@react-keycloak/web', () => ({
  useKeycloak: () => ({
    keycloak: mockKeycloak,
  }),
}))

describe('HeroSection', () => {
  it('renders the title and subtitle', () => {
    render(<HeroSection />)

    expect(screen.getByText('home.title')).toBeInTheDocument()
    expect(screen.getByText('home.subtitle')).toBeInTheDocument()
  })

  it('shows login button when not authenticated', () => {
    mockKeycloak.authenticated = false
    render(<HeroSection />)

    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument()
  })

  it('hides login button when authenticated', () => {
    mockKeycloak.authenticated = true
    render(<HeroSection />)

    expect(screen.queryByRole('button', { name: /login/i })).not.toBeInTheDocument()
  })
})
