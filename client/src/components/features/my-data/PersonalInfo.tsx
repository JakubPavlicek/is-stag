import { useState } from 'react'
import { useTranslation } from 'react-i18next'

import { Edit } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { PersonalInfoForm } from '@/components/features/my-data/forms/PersonalInfoForm'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { formatDate } from '@/lib/utils'

type Person = components['schemas']['PersonResponse']

export function PersonalInfo({ person }: Readonly<{ person: Person }>) {
  const { t, i18n } = useTranslation()
  const [open, setOpen] = useState(false)

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
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.title_before')}
            </p>
            <p className="font-medium">{person.titles?.prefix || '-'}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.title_after')}
            </p>
            <p className="font-medium">{person.titles?.suffix || '-'}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.birth_number')}
            </p>
            <p className="font-medium">{person.birthNumber || '-'}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.birth_date')}
            </p>
            <p className="font-medium">
              {person.birthDate ? formatDate(person.birthDate, i18n.language) : '-'}
            </p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.birth_surname')}
            </p>
            <p className="font-medium">{person.birthSurname || '-'}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.citizenship')}
            </p>
            <p className="font-medium">
              {person.citizenship?.country || '-'}
              {person.citizenship?.qualifier && ` (${person.citizenship.qualifier})`}
            </p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.birth_place_country')}
            </p>
            <p className="font-medium">{person.birthPlace?.country || '-'}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.birth_place_city')}
            </p>
            <p className="font-medium">{person.birthPlace?.city || '-'}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.personal_info.marital_status')}
            </p>
            <p className="font-medium">{person.maritalStatus || '-'}</p>
          </div>
        </div>
      </CardContent>
      <PersonalInfoForm person={person} open={open} onOpenChange={setOpen} />
    </Card>
  )
}
