import { useState } from 'react'
import { useTranslation } from 'react-i18next'

import { Edit } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { PersonalInfoForm } from '@/components/features/my-data/forms/PersonalInfoForm'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { formatDate } from '@/lib/utils'

type Person = components['schemas']['PersonResponse']

/** Renders a single personal info item (label-value pair). */
function PersonalItem({ label, value }: Readonly<{ label: string; value: string | null }>) {
  return (
    <div>
      <p className="text-muted-foreground text-sm font-medium">{label}</p>
      <p className="font-medium">{value || '-'}</p>
    </div>
  )
}

/**
 * Displays user's personal information (titles, birth details, citizenship, etc.).
 * - Includes an edit button that opens a modal form (`PersonalInfoForm`).
 */
export function PersonalInfo({ person }: Readonly<{ person: Person }>) {
  const { t, i18n } = useTranslation()
  const [open, setOpen] = useState(false)

  // Construct citizenship display string (country + qualifier if available)
  const citizenship = [person.citizenship?.country, person.citizenship?.qualifier]
    .filter(Boolean)
    .join(' ')

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle>{t('my_data.personal_info.title')}</CardTitle>
        <Button variant="ghost" size="icon" onClick={() => setOpen(true)}>
          <Edit className="h-4 w-4" />
        </Button>
      </CardHeader>
      <CardContent className="pt-4">
        <div className="grid grid-cols-1 gap-x-8 gap-y-4 md:grid-cols-2">
          <PersonalItem
            label={t('my_data.personal_info.title_before')}
            value={person.titles?.prefix}
          />
          <PersonalItem
            label={t('my_data.personal_info.title_after')}
            value={person.titles?.suffix}
          />
          <PersonalItem
            label={t('my_data.personal_info.birth_number')}
            value={person.birthNumber}
          />
          <PersonalItem
            label={t('my_data.personal_info.birth_date')}
            value={person.birthDate ? formatDate(person.birthDate, i18n.language) : null}
          />
          <PersonalItem
            label={t('my_data.personal_info.birth_surname')}
            value={person.birthSurname}
          />
          <PersonalItem label={t('my_data.personal_info.citizenship')} value={citizenship} />
          <PersonalItem
            label={t('my_data.personal_info.birth_place_country')}
            value={person.birthPlace?.country}
          />
          <PersonalItem
            label={t('my_data.personal_info.birth_place_city')}
            value={person.birthPlace?.city}
          />
          <PersonalItem
            label={t('my_data.personal_info.marital_status')}
            value={person.maritalStatus}
          />
        </div>
      </CardContent>
      <PersonalInfoForm person={person} open={open} onOpenChange={setOpen} />
    </Card>
  )
}
