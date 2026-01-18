import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { ModeToggle } from '../mode-toggle'

// Mock useTheme hook
const mockSetTheme = vi.fn()
vi.mock('../theme-provider', () => ({
  useTheme: () => ({
    theme: 'light',
    setTheme: mockSetTheme,
  }),
}))

describe('ModeToggle', () => {
  it('renders toggle button', () => {
    render(<ModeToggle />)
    expect(screen.getByRole('button', { name: /toggle theme/i })).toBeInTheDocument()
  })

  it('toggles theme on click', () => {
    render(<ModeToggle />)
    const button = screen.getByRole('button', { name: /toggle theme/i })

    // Simulate click
    fireEvent.click(button)

    // Since the mock theme is 'light', clicking should set it to 'dark'
    expect(mockSetTheme).toHaveBeenCalledWith('dark')
  })
})
