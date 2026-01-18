import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { BasicInfo } from '../BasicInfo'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

describe('BasicInfo', () => {
  const mockStudent = {
    studentId: 'S123',
    firstName: 'John',
    lastName: 'Doe',
    titles: { prefix: 'Ing.', suffix: null },
    studyProgram: { name: 'CS' },
    fieldOfStudy: { name: 'AI' },
  } as any

  it('renders basic info correctly', () => {
    render(<BasicInfo student={mockStudent} />)
    expect(screen.getByText('my_data.basic_info.name')).toBeInTheDocument()
    expect(screen.getByText('Ing. John Doe')).toBeInTheDocument()

    expect(screen.getByText('my_data.basic_info.personal_number')).toBeInTheDocument()
    expect(screen.getByText('S123')).toBeInTheDocument()

    expect(screen.getByText('my_data.basic_info.study_program')).toBeInTheDocument()
    expect(screen.getByText('CS')).toBeInTheDocument()

    expect(screen.getByText('my_data.basic_info.field_of_study')).toBeInTheDocument()
    expect(screen.getByText('AI')).toBeInTheDocument()
  })
})
