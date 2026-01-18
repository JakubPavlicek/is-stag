import type { ReactNode } from 'react'

import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { DashboardSection } from '../DashboardSection'

// Mock react-i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string, options?: { name: string }) => {
      if (key === 'home.welcome_user' && options?.name) {
        return `Welcome back, ${options.name}`
      }
      return key
    },
  }),
}))

// Mock @tanstack/react-router
vi.mock('@tanstack/react-router', () => ({
  Link: ({ to, children, className }: { to: string; children: ReactNode; className?: string }) => (
    <a href={to} className={className} data-testid="link">
      {children}
    </a>
  ),
}))

describe('DashboardSection', () => {
  it('renders the welcome message with user name', () => {
    render(<DashboardSection userName="John Doe" />)
    expect(screen.getByText('Welcome back, John Doe')).toBeInTheDocument()
  })

  it('renders all dashboard cards', () => {
    render(<DashboardSection userName="Test User" />)

    expect(screen.getByText('home.cards.my_data')).toBeInTheDocument()
    expect(screen.getByText('home.cards.study')).toBeInTheDocument()
    expect(screen.getByText('home.cards.schedule')).toBeInTheDocument()
    expect(screen.getByText('home.cards.news')).toBeInTheDocument()
  })

  it('renders the My Data card with correct link', () => {
    render(<DashboardSection userName="Test User" />)

    // The Link component is mocked to render an <a> tag with data-testid="link"
    const link = screen.getByTestId('link')
    expect(link).toHaveAttribute('href', '/my-data')
    expect(screen.getByText('home.cards.my_data_desc')).toBeInTheDocument()
  })

  it('renders placeholders for inactive features', () => {
    render(<DashboardSection userName="Test User" />)

    // There are 3 "Coming Soon" badges in the component
    const comingSoonBadges = screen.getAllByText('common.coming_soon')
    expect(comingSoonBadges).toHaveLength(3)
  })
})
