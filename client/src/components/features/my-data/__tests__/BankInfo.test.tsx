import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { BankInfo } from '../BankInfo'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

vi.mock('../forms/BankInfoForm', () => ({
  BankInfoForm: ({ open }: { open: boolean }) => (
    <div data-testid="bank-form">{open ? 'Open' : 'Closed'}</div>
  ),
}))

describe('BankInfo', () => {
  const mockBanking = {
    account: {
      accountNumberPrefix: '123',
      accountNumberSuffix: '456789',
      bankCode: '0100',
      bankName: 'Test Bank',
      holderName: 'John Doe',
      iban: 'CZ123',
      holderAddress: null,
    },
    euroAccount: null,
  } as any

  it('renders standard account details', () => {
    render(<BankInfo banking={mockBanking} personId={1} />)
    expect(screen.getByText('my_data.bank.account_standard')).toBeInTheDocument()
    expect(screen.getByText('123-456789/0100')).toBeInTheDocument()
    expect(screen.getByText('Test Bank')).toBeInTheDocument()
    expect(screen.getByText('CZ123')).toBeInTheDocument()
    expect(screen.getByText('John Doe')).toBeInTheDocument()
  })

  it('opens edit form when button is clicked', () => {
    render(<BankInfo banking={mockBanking} personId={1} />)
    const editButton = screen.getByRole('button')
    fireEvent.click(editButton)
    expect(screen.getByTestId('bank-form')).toHaveTextContent('Open')
  })
})
