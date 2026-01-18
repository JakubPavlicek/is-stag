import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { PersonalInfoForm } from '../PersonalInfoForm'

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
      values: [{ key: 'Ing.', name: 'Ing.', abbreviation: 'Ing.' }],
    },
  }),
  useCountries: () => ({
    data: {
      countries: [{ name: 'Czech Republic' }],
    },
  }),
}))

vi.mock('@/lib/api', () => ({
  $user: {
    useMutation: () => ({ mutateAsync: vi.fn() }),
  },
}))

describe('PersonalInfoForm', () => {
  const defaultProps = {
    person: {
      personId: 1,
      titles: { prefix: 'Ing.', suffix: '' },
      birthSurname: 'Smith',
      maritalStatus: 'Single',
      birthPlace: { country: 'Czech Republic', city: 'Prague' },
    } as any,
    open: true,
    onOpenChange: vi.fn(),
  }

  it('renders form with initial values', () => {
    render(<PersonalInfoForm {...defaultProps} />)
    expect(screen.getAllByText('Ing.')).toHaveLength(1) // In combobox
    expect(screen.getByDisplayValue('Smith')).toBeInTheDocument()
    expect(screen.getByDisplayValue('Prague')).toBeInTheDocument()
  })

  it('submits form correctly', async () => {
    render(<PersonalInfoForm {...defaultProps} />)

    const surnameInput = screen.getByDisplayValue('Smith')
    fireEvent.change(surnameInput, { target: { value: 'Doe' } })
    fireEvent.blur(surnameInput)

    const submitBtn = screen.getByText('save')
    fireEvent.click(submitBtn)

    await waitFor(() => {
      expect(mockHandleSubmit).toHaveBeenCalled()
    })
  })
})
