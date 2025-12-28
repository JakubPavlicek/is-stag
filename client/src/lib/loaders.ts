import type { QueryClient } from '@tanstack/react-query'

import i18n from '@/i18n.ts'
import { $student, $user } from '@/lib/api'

/**
 * Preloads student data and related person information.
 * - Fetches basic student details.
 * - If a person ID is found, fetches personal details, addresses, and banking info in parallel.
 * - Uses `ensureQueryData` to check the cache before fetching.
 */
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

  // If no person ID is associated with the student, return early with null for person-related data.
  if (!personId) {
    return { student, person: null, addresses: null, banking: null }
  }

  // Fetch all related person data in parallel to optimize load time.
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
