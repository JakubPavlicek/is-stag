import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { BankInfoForm } from '../BankInfoForm'

// Mock dependencies
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

vi.mock('@/hooks/use-codelists', () => ({
  useCodelist: () => ({
    data: {
      values: [{ key: '0100', name: 'Komerční banka', abbreviation: '0100' }],
    },
  }),
}))

vi.mock('@/lib/api', () => ({
  $user: {
    useMutation: () => ({ mutateAsync: vi.fn() }),
  },
}))

describe('BankInfoForm', () => {
  const defaultProps = {
    personId: 1,
    open: true,
    onOpenChange: vi.fn(),
    account: {
      accountNumberPrefix: '123',
      accountNumberSuffix: '456789',
      bankCode: '0100',
      holderName: 'John Doe',
      holderAddress: 'Address',
    } as any,
  }

  it('renders form fields with initial values', () => {
    render(<BankInfoForm {...defaultProps} />)

    expect(screen.getByDisplayValue('123')).toBeInTheDocument()
    expect(screen.getByDisplayValue('456789')).toBeInTheDocument()
    expect(screen.getByText(/0100 - Komerční banka/)).toBeInTheDocument()
    expect(screen.getByDisplayValue('John Doe')).toBeInTheDocument()
  })

  it('submits form with updated values', async () => {
    render(<BankInfoForm {...defaultProps} />)

    const prefixInput = screen.getByDisplayValue('123')
    fireEvent.change(prefixInput, { target: { value: '999' } })
    fireEvent.blur(prefixInput)

    const submitBtn = screen.getByText('save')

    await waitFor(() => {
      expect(submitBtn).not.toBeDisabled()
    })

    fireEvent.click(submitBtn)

    await waitFor(() => {
      expect(mockHandleSubmit).toHaveBeenCalled()
    })
  })
})
