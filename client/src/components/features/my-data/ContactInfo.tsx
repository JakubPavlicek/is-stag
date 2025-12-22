import type { ElementType } from 'react'
import { useTranslation } from 'react-i18next'

import { AtSign, Box, Phone, Smartphone } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type Contact = components['schemas']['Contact']

function ContactItem({
  icon: Icon,
  label,
  value,
}: Readonly<{ icon: ElementType; label: string; value?: string | null }>) {
  return (
    <div className="flex items-center gap-3 rounded-lg border p-3">
      <div className="bg-muted flex h-9 w-9 shrink-0 items-center justify-center rounded-full">
        <Icon className="text-primary h-4 w-4" />
      </div>
      <div className="grid gap-0.5">
        <p className="text-muted-foreground text-xs font-medium tracking-wider uppercase">
          {label}
        </p>
        <p className="text-sm font-medium">{value || '-'}</p>
      </div>
    </div>
  )
}

export function ContactInfo({ contact }: Readonly<{ contact: Contact }>) {
  const { t } = useTranslation()

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('my_data.contact.title')}</CardTitle>
      </CardHeader>
      <CardContent className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <ContactItem icon={AtSign} label={t('my_data.contact.email')} value={contact.email} />
        <ContactItem icon={Phone} label={t('my_data.contact.phone')} value={contact.phone} />
        <ContactItem icon={Smartphone} label={t('my_data.contact.mobile')} value={contact.mobile} />
        <ContactItem icon={Box} label={t('my_data.contact.databox')} value={contact.dataBox} />
      </CardContent>
    </Card>
  )
}
