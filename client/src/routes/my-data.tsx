import { useTranslation } from 'react-i18next'

import { useKeycloak } from '@react-keycloak/web'
import { createFileRoute } from '@tanstack/react-router'

import { AddressInfo } from '@/components/features/my-data/AddressInfo'
import { BankInfo } from '@/components/features/my-data/BankInfo'
import { BasicInfo } from '@/components/features/my-data/BasicInfo'
import { ContactInfo } from '@/components/features/my-data/ContactInfo'
import { PersonalInfo } from '@/components/features/my-data/PersonalInfo'
import { $student, $user } from '@/lib/api'

export const Route = createFileRoute('/my-data')({
  component: MyData,
})

function MyData() {
  const { keycloak } = useKeycloak()
  const { t, i18n } = useTranslation()
  const studentId = keycloak.tokenParsed?.studentId
  const lang = (i18n.language?.split('-')[0] || 'cs') as 'cs' | 'en'

  const { data: student, isLoading: isStudentLoading } = $student.useQuery(
    'get',
    '/students/{studentId}',
    {
      params: {
        path: { studentId: studentId! },
        header: { 'Accept-Language': lang },
      },
      enabled: !!studentId,
    },
  )

  const personId = student?.personId

  const { data: person } = $user.useQuery('get', '/persons/{personId}', {
    params: {
      path: { personId: personId! },
      header: { 'Accept-Language': lang },
    },
    enabled: !!personId,
  })

  const { data: addresses } = $user.useQuery('get', '/persons/{personId}/addresses', {
    params: {
      path: { personId: personId! },
      header: { 'Accept-Language': lang },
    },
    enabled: !!personId,
  })

  const { data: banking } = $user.useQuery('get', '/persons/{personId}/banking', {
    params: {
      path: { personId: personId! },
      header: { 'Accept-Language': lang },
    },
    enabled: !!personId,
  })

  if (isStudentLoading)
    return (
      <div className="text-muted-foreground flex justify-center p-8">{t('my_data.loading')}</div>
    )
  if (!student)
    return (
      <div className="text-muted-foreground p-8 text-center">
        {t('my_data.error_not_available')}
      </div>
    )

  return (
    <div className="mx-auto max-w-5xl space-y-6 p-6">
      <h1 className="mb-6 text-3xl font-bold tracking-tight">{t('my_data.title')}</h1>

      <div className="grid gap-6">
        <BasicInfo student={student} />
        {addresses && <AddressInfo addresses={addresses} />}
        {person?.contact && <ContactInfo contact={person.contact} />}
        {person && <PersonalInfo person={person} />}
        {banking && <BankInfo banking={banking} />}
      </div>
    </div>
  )
}
