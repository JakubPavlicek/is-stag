import { describe, expect, it, vi } from 'vitest'

import { $student, $user } from '../api'
import { loadStudentData } from '../loaders'

// Mock dependencies
vi.mock('@/i18n.ts', () => ({
  default: { language: 'cs' },
}))

vi.mock('../api', () => ({
  $student: {
    queryOptions: vi.fn(),
  },
  $user: {
    queryOptions: vi.fn(),
  },
}))

describe('loadStudentData', () => {
  const mockQueryClient = {
    ensureQueryData: vi.fn(),
  } as any

  it('fetches only student if personId is missing', async () => {
    // Mock student response without personId
    mockQueryClient.ensureQueryData.mockResolvedValueOnce({ studentId: 'S123' })

    const result = await loadStudentData(mockQueryClient, 'S123')

    expect($student.queryOptions).toHaveBeenCalled()
    expect($user.queryOptions).not.toHaveBeenCalled()
    expect(result).toEqual({
      student: { studentId: 'S123' },
      person: null,
      addresses: null,
      banking: null,
    })
  })

  it('fetches all data if personId is present', async () => {
    // Mock responses
    mockQueryClient.ensureQueryData
      .mockResolvedValueOnce({ studentId: 'S123', personId: 1 }) // student
      .mockResolvedValueOnce({ personId: 1 }) // person
      .mockResolvedValueOnce({ permanentAddress: {} }) // addresses
      .mockResolvedValueOnce({ account: {} }) // banking

    const result = await loadStudentData(mockQueryClient, 'S123')

    expect($student.queryOptions).toHaveBeenCalled()
    expect($user.queryOptions).toHaveBeenCalledTimes(3) // person, addresses, banking
    expect(result).toEqual({
      student: { studentId: 'S123', personId: 1 },
      person: { personId: 1 },
      addresses: { permanentAddress: {} },
      banking: { account: {} },
    })
  })
})
