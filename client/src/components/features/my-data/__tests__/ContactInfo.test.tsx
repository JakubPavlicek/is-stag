import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { ContactInfo } from '../ContactInfo'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

vi.mock('../forms/ContactForm', () => ({
  ContactForm: ({ open }: { open: boolean }) => (
    <div data-testid="contact-form">{open ? 'Open' : 'Closed'}</div>
  ),
}))

describe('ContactInfo', () => {
  const mockContact = {
    email: 'test@example.com',
    phone: '123456789',
    mobile: '987654321',
    dataBox: 'abcdef',
  }

  it('renders contact details', () => {
    render(<ContactInfo contact={mockContact} personId={1} />)
    expect(screen.getByText('my_data.contact.email')).toBeInTheDocument()
    expect(screen.getByText('test@example.com')).toBeInTheDocument()

    expect(screen.getByText('my_data.contact.phone')).toBeInTheDocument()
    expect(screen.getByText('123456789')).toBeInTheDocument()
  })

  it('opens edit form', () => {
    render(<ContactInfo contact={mockContact} personId={1} />)
    const editBtn = screen.getByRole('button')
    fireEvent.click(editBtn)
    expect(screen.getByTestId('contact-form')).toHaveTextContent('Open')
  })
})
