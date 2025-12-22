import { useTranslation } from 'react-i18next'

import type { components } from '@/api/student/schema'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type Student = components['schemas']['StudentResponse']

export function BasicInfo({ student }: Readonly<{ student: Student }>) {
  const { t } = useTranslation()

  const fullTitle = [
    student.titles?.prefix,
    student.firstName,
    student.lastName,
    student.titles?.suffix,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('my_data.basic_info.title')}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 gap-x-8 gap-y-4 md:grid-cols-2">
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.basic_info.name')}
            </p>
            <p className="font-medium">{fullTitle}</p>
          </div>
          <div>
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.basic_info.personal_number')}
            </p>
            <p className="font-medium">{student.studentId}</p>
          </div>
          <div className="md:col-span-2">
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.basic_info.study_program')}
            </p>
            <p className="font-medium">{student.studyProgram?.name || '-'}</p>
          </div>
          <div className="md:col-span-2">
            <p className="text-muted-foreground text-sm font-medium">
              {t('my_data.basic_info.field_of_study')}
            </p>
            <p className="font-medium">{student.fieldOfStudy?.name || '-'}</p>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
