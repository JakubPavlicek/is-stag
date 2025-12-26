import { useTranslation } from 'react-i18next'

import { createFileRoute } from '@tanstack/react-router'

import { AddressInfo } from '@/components/features/my-data/AddressInfo'
import { BankInfo } from '@/components/features/my-data/BankInfo'
import { BasicInfo } from '@/components/features/my-data/BasicInfo'
import { ContactInfo } from '@/components/features/my-data/ContactInfo'
import { PersonalInfo } from '@/components/features/my-data/PersonalInfo'
import { keycloak } from '@/lib/auth'
import { loadStudentData } from '@/lib/loaders'

export const Route = createFileRoute('/my-data')({
  component: MyData,
  loader: async ({ context: { queryClient } }) => {
    const studentId = keycloak.tokenParsed?.studentId

    if (!studentId) {
      return { student: null, person: null, addresses: null, banking: null }
    }

    return loadStudentData(queryClient, studentId)
  },
})

function MyData() {
  const { t } = useTranslation()
  const { student, person, addresses, banking } = Route.useLoaderData()

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
