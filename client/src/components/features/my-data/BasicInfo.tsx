import { useTranslation } from 'react-i18next'

import type { components } from '@/api/student/schema'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { cn } from '@/lib/utils'

type Student = components['schemas']['StudentResponse']

/** Renders a single basic info item (label-value pair). */
function BasicItem({
  label,
  value,
  className,
}: Readonly<{ label: string; value: string; className?: string }>) {
  return (
    <div className={cn(className)}>
      <p className="text-muted-foreground text-sm font-medium">{label}</p>
      <p className="font-medium">{value}</p>
    </div>
  )
}

/**
 * Displays basic student information.
 * - Shows name (with titles), personal number (student ID), study program, and field of study.
 */
export function BasicInfo({ student }: Readonly<{ student: Student }>) {
  const { t } = useTranslation()

  // Construct full name with academic titles (prefix + name + suffix), filtering out empty values
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
          <BasicItem label={t('my_data.basic_info.name')} value={fullTitle} />
          <BasicItem label={t('my_data.basic_info.personal_number')} value={student.studentId} />
          <BasicItem
            label={t('my_data.basic_info.study_program')}
            value={student.studyProgram?.name || '-'}
            className="md:col-span-2"
          />
          <BasicItem
            label={t('my_data.basic_info.field_of_study')}
            value={student.fieldOfStudy?.name || '-'}
            className="md:col-span-2"
          />
        </div>
      </CardContent>
    </Card>
  )
}
