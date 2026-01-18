import type { ComponentType } from 'react'

import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { Route } from '../my-data'

// Mock dependencies
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  initReactI18next: {
    type: '3rdParty',
    init: vi.fn(),
  },
}))

vi.mock('@tanstack/react-router', () => ({
  createFileRoute: () => (config: any) => ({
    ...config,
    useLoaderData: vi.fn(),
  }),
}))

// Mock Feature components
vi.mock('@/components/features/my-data/BasicInfo', () => ({
  BasicInfo: () => <div data-testid="basic-info">Basic Info</div>,
}))
vi.mock('@/components/features/my-data/AddressInfo', () => ({
  AddressInfo: () => <div data-testid="address-info">Address Info</div>,
}))
vi.mock('@/components/features/my-data/ContactInfo', () => ({
  ContactInfo: () => <div data-testid="contact-info">Contact Info</div>,
}))
vi.mock('@/components/features/my-data/PersonalInfo', () => ({
  PersonalInfo: () => <div data-testid="personal-info">Personal Info</div>,
}))
vi.mock('@/components/features/my-data/BankInfo', () => ({
  BankInfo: () => <div data-testid="bank-info">Bank Info</div>,
}))

describe('MyDataRoute', () => {
  const MyDataComponent = (Route as any).component as ComponentType
  const useLoaderData = Route.useLoaderData as any

  it('renders error when no student data', () => {
    useLoaderData.mockReturnValue({ student: null })
    render(<MyDataComponent />)

    expect(screen.getByText('my_data.error_not_available')).toBeInTheDocument()
  })

  it('renders all sections when data is present', () => {
    useLoaderData.mockReturnValue({
      student: { studentId: 'S123' },
      person: { personId: 1, contact: {} },
      addresses: {},
      banking: {},
    })

    render(<MyDataComponent />)

    expect(screen.getByText('my_data.title')).toBeInTheDocument()
    expect(screen.getByTestId('basic-info')).toBeInTheDocument()
    expect(screen.getByTestId('address-info')).toBeInTheDocument()
    expect(screen.getByTestId('contact-info')).toBeInTheDocument()
    expect(screen.getByTestId('personal-info')).toBeInTheDocument()
    expect(screen.getByTestId('bank-info')).toBeInTheDocument()
  })
})
