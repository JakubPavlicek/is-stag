import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { PublicFeatures } from '../PublicFeatures'

// Mock react-i18next
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

describe('PublicFeatures', () => {
  it('renders all public feature sections', () => {
    render(<PublicFeatures />)

    // Check titles
    expect(screen.getByText('home.public.portal_title')).toBeInTheDocument()
    expect(screen.getByText('home.public.scheduling_title')).toBeInTheDocument()
    expect(screen.getByText('home.public.academics_title')).toBeInTheDocument()

    // Check descriptions
    expect(screen.getByText('home.public.portal_desc')).toBeInTheDocument()
    expect(screen.getByText('home.public.scheduling_desc')).toBeInTheDocument()
    expect(screen.getByText('home.public.academics_desc')).toBeInTheDocument()
  })
})
