import { useTranslation } from 'react-i18next'

import { createFileRoute, useRouter } from '@tanstack/react-router'
import { useEffect } from 'react'

import { AddressInfo } from '@/components/features/my-data/AddressInfo'
import { BankInfo } from '@/components/features/my-data/BankInfo'
import { BasicInfo } from '@/components/features/my-data/BasicInfo'
import { ContactInfo } from '@/components/features/my-data/ContactInfo'
import { PersonalInfo } from '@/components/features/my-data/PersonalInfo'
import i18n from '@/i18n'
import { $student, $user } from '@/lib/api'
import { keycloak } from '@/lib/auth'

export const Route = createFileRoute('/my-data')({
  component: MyData,
  loader: async ({ context: { queryClient } }) => {
    const studentId = keycloak.tokenParsed?.studentId
    const lang = (i18n.language?.split('-')[0] || 'cs') as 'cs' | 'en'

    if (!studentId) {
      return { student: null, person: null, addresses: null, banking: null }
    }

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
  },
})

function MyData() {
  const { t, i18n } = useTranslation()
  const { student, person, addresses, banking } = Route.useLoaderData()
  const router = useRouter()

  useEffect(() => {
    router.invalidate()
  }, [i18n.language, router])

  if (!student) {
    return (
      <div className="text-muted-foreground p-8 text-center">
        {t('my_data.error_not_available')}
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-5xl space-y-6 p-6">
      <h1 className="mb-6 text-3xl font-bold tracking-tight">{t('my_data.title')}</h1>

      <div className="grid gap-6">
        <BasicInfo student={student} />
        {addresses && <AddressInfo addresses={addresses} />}
        {person?.contact && <ContactInfo contact={person.contact} personId={person.personId} />}
        {person && <PersonalInfo person={person} />}
        {banking && person && <BankInfo banking={banking} personId={person.personId} />}
      </div>
    </div>
  )
}
