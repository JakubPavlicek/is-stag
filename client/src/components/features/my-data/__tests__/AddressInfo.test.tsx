import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { AddressInfo } from '../AddressInfo'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

describe('AddressInfo', () => {
  const mockAddresses = {
    permanentAddress: {
      street: 'Main St',
      streetNumber: '123',
      zipCode: '12345',
      municipality: 'City',
      district: 'District',
      country: 'Country',
      municipalityPart: null,
    },
    temporaryAddress: null,
    foreignPermanentAddress: null,
    foreignTemporaryAddress: null,
  } as any

  it('renders permanent address correctly', () => {
    render(<AddressInfo addresses={mockAddresses} />)
    expect(screen.getByText('my_data.addresses.permanent')).toBeInTheDocument()
    expect(screen.getByText(/Main St 123/)).toBeInTheDocument()
    expect(screen.getByText(/12345 City/)).toBeInTheDocument()
    expect(screen.getByText(/District • Country/)).toBeInTheDocument()
  })

  it('renders empty temporary address correctly', () => {
    render(<AddressInfo addresses={mockAddresses} />)
    expect(screen.getByText('my_data.addresses.temporary')).toBeInTheDocument()
    expect(screen.getAllByText('my_data.addresses.not_provided')).toHaveLength(1)
  })
})
