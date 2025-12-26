import type { QueryClient } from '@tanstack/react-query'

import i18n from '@/i18n.ts'
import { $student, $user } from '@/lib/api'

export async function loadStudentData(queryClient: QueryClient, studentId: string) {
  const lang = i18n.language as 'cs' | 'en'

  const student = await queryClient.ensureQueryData(
    $student.queryOptions('get', '/students/{studentId}', {
      params: {
        path: { studentId },
        header: { 'Accept-Language': lang },
      },
    }),
  )

  const personId = student?.personId

  if (!personId) {
    return { student, person: null, addresses: null, banking: null }
  }

  const [person, addresses, banking] = await Promise.all([
    queryClient.ensureQueryData(
      $user.queryOptions('get', '/persons/{personId}', {
        params: {
          path: { personId },
          header: { 'Accept-Language': lang },
        },
      }),
    ),
    queryClient.ensureQueryData(
      $user.queryOptions('get', '/persons/{personId}/addresses', {
        params: {
          path: { personId },
          header: { 'Accept-Language': lang },
        },
      }),
    ),
    queryClient.ensureQueryData(
      $user.queryOptions('get', '/persons/{personId}/banking', {
        params: {
          path: { personId },
          header: { 'Accept-Language': lang },
        },
      }),
    ),
  ])

  return { student, person, addresses, banking }
}
