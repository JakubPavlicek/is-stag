import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'

import { ContactForm } from '../ContactForm'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
    i18n: { exists: () => false },
  }),
}))

const mockHandleSubmit = vi.fn()
vi.mock('@/hooks/use-form-submit', () => ({
  useFormSubmit: () => ({ handleSubmit: mockHandleSubmit }),
}))

vi.mock('@/lib/api', () => ({
  $user: {
    useMutation: () => ({ mutateAsync: vi.fn() }),
  },
}))

describe('ContactForm', () => {
  const defaultProps = {
    personId: 1,
    open: true,
    onOpenChange: vi.fn(),
    contact: {
      email: 'test@example.com',
      phone: '123',
      mobile: '456',
      dataBox: 'abcdefg',
    },
  }

  it('renders form fields', () => {
    render(<ContactForm {...defaultProps} />)
    expect(screen.getByDisplayValue('test@example.com')).toBeInTheDocument()
    expect(screen.getByDisplayValue('123')).toBeInTheDocument()
  })

  it('submits form correctly', async () => {
    const user = userEvent.setup()
    render(<ContactForm {...defaultProps} />)

    const emailInput = screen.getByDisplayValue('test@example.com')
    await user.clear(emailInput)
    await user.type(emailInput, 'new@example.com')

    const submitBtn = screen.getByText('save')

    // Wait for validation/form state update
    await waitFor(() => {
      expect(submitBtn).not.toBeDisabled()
    })

    await user.click(submitBtn)

    await waitFor(() => {
      expect(mockHandleSubmit).toHaveBeenCalled()
    })
  })
})
