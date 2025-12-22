import { useTranslation } from 'react-i18next'

import type { components } from '@/api/user/schema'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { formatDate } from '@/lib/utils'

type Person = components['schemas']['PersonResponse']

export function PersonalInfo({ person }: Readonly<{ person: Person }>) {
  const { t, i18n } = useTranslation()

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('my_data.personal_info.title')}</CardTitle>
      </CardHeader>
      <CardContent>
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
    </Card>
  )
}
