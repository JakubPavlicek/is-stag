import { fireEvent, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'

import { Header } from '../Header'

// Mock react-i18next
const mockChangeLanguage = vi.fn()
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
    i18n: {
      changeLanguage: mockChangeLanguage,
      language: 'cs',
    },
  }),
}))

// Mock react-keycloak
const mockKeycloak = {
  authenticated: false,
  login: vi.fn(),
  logout: vi.fn(),
  tokenParsed: {
    name: 'John Doe',
    preferred_username: 'jdoe',
  },
}

vi.mock('@react-keycloak/web', () => ({
  useKeycloak: () => ({
    keycloak: mockKeycloak,
  }),
}))

// Mock theme provider
vi.mock('@/components/theme/theme-provider', () => ({
  useTheme: () => ({
    theme: 'light',
    setTheme: vi.fn(),
  }),
}))

// Mock Sidebar component since it's used in mobile view
vi.mock('../Sidebar', () => ({
  Sidebar: () => <div data-testid="mock-sidebar">Sidebar</div>,
}))

describe('Header', () => {
  it('renders login button when not authenticated', () => {
    mockKeycloak.authenticated = false
    render(<Header />)
    expect(screen.getByText('login')).toBeInTheDocument()
  })

  it('renders user avatar and logout when authenticated', () => {
    mockKeycloak.authenticated = true
    render(<Header />)
    expect(screen.queryByText('login')).not.toBeInTheDocument()

    // User avatar fallback should be JO (John Doe -> JO)
    expect(screen.getByText('JO')).toBeInTheDocument()
  })

  it('calls login on button click', () => {
    mockKeycloak.authenticated = false
    render(<Header />)
    fireEvent.click(screen.getByText('login'))
    expect(mockKeycloak.login).toHaveBeenCalled()
  })

  it('allows language switching', async () => {
    const user = userEvent.setup()
    render(<Header />)

    // Open language dropdown (Globe icon)
    const languageTrigger = screen.getByRole('button', { name: 'Switch language' })
    await user.click(languageTrigger)

    // Select English
    // Use findByRole to avoid ambiguity with the flag icon title
    const englishOption = await screen.findByRole('menuitem', { name: /English/i })
    await user.click(englishOption)

    expect(mockChangeLanguage).toHaveBeenCalledWith('en')
  })
})
