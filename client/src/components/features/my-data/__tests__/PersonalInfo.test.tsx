import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { PersonalInfo } from '../PersonalInfo'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
    i18n: { language: 'en' },
  }),
}))

vi.mock('@/lib/utils', async (importOriginal) => {
  const actual = await importOriginal<typeof import('@/lib/utils')>()
  return {
    ...actual,
    formatDate: () => '01/01/2000',
  }
})

vi.mock('../forms/PersonalInfoForm', () => ({
  PersonalInfoForm: ({ open }: { open: boolean }) => (
    <div data-testid="personal-form">{open ? 'Open' : 'Closed'}</div>
  ),
}))

describe('PersonalInfo', () => {
  const mockPerson = {
    titles: { prefix: 'Bc.', suffix: null },
    birthNumber: '000101/1234',
    birthDate: '2000-01-01',
    birthSurname: 'Smith',
    citizenship: { country: 'USA', qualifier: null },
    birthPlace: { country: 'USA', city: 'NY' },
    maritalStatus: 'Single',
  } as any

  it('renders personal details', () => {
    render(<PersonalInfo person={mockPerson} />)
    expect(screen.getByText('my_data.personal_info.title_before')).toBeInTheDocument()
    expect(screen.getByText('Bc.')).toBeInTheDocument()

    expect(screen.getByText('my_data.personal_info.birth_date')).toBeInTheDocument()
    expect(screen.getByText('01/01/2000')).toBeInTheDocument() // from mock
  })

  it('opens edit form', () => {
    render(<PersonalInfo person={mockPerson} />)
    const editBtn = screen.getByRole('button')
    fireEvent.click(editBtn)
    expect(screen.getByTestId('personal-form')).toHaveTextContent('Open')
  })
})
