import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { Sidebar } from '../Sidebar'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

vi.mock('@tanstack/react-router', () => ({
  useRouterState: () => ({
    location: { pathname: '/' },
  }),
  Link: ({ to, children, onClick }: any) => (
    <a href={to} onClick={onClick}>
      {children}
    </a>
  ),
}))

describe('Sidebar', () => {
  it('renders navigation links', () => {
    render(<Sidebar />)
    expect(screen.getByText('sidebar.home')).toBeInTheDocument()
    expect(screen.getByText('sidebar.my_data')).toBeInTheDocument()
  })

  it('calls onNavigate when a link is clicked', () => {
    const onNavigate = vi.fn()
    render(<Sidebar onNavigate={onNavigate} />)

    fireEvent.click(screen.getByText('sidebar.home'))
    expect(onNavigate).toHaveBeenCalled()
  })
})
