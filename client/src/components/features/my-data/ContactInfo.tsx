import { type ElementType, useState } from 'react'
import { useTranslation } from 'react-i18next'

import { AtSign, Box, Edit, Phone, Smartphone } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { ContactForm } from '@/components/features/my-data/forms/ContactForm'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type Contact = components['schemas']['Contact']

function ContactItem({
  icon: Icon,
  label,
  value,
}: Readonly<{ icon: ElementType; label: string; value?: string | null }>) {
  return (
    <div className="hover:bg-accent/20 flex items-center gap-3 rounded-lg border p-3 hover:shadow-md">
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

export function ContactInfo({
  contact,
  personId,
}: Readonly<{ contact: Contact; personId: number }>) {
  const { t } = useTranslation()
  const [open, setOpen] = useState(false)

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle>{t('my_data.contact.title')}</CardTitle>
        <Button variant="ghost" size="icon" onClick={() => setOpen(true)}>
          <Edit className="h-4 w-4" />
        </Button>
      </CardHeader>
      <CardContent className="grid grid-cols-1 gap-4 pt-4 lg:grid-cols-2">
        <ContactItem icon={AtSign} label={t('my_data.contact.email')} value={contact.email} />
        <ContactItem icon={Phone} label={t('my_data.contact.phone')} value={contact.phone} />
        <ContactItem icon={Smartphone} label={t('my_data.contact.mobile')} value={contact.mobile} />
        <ContactItem icon={Box} label={t('my_data.contact.databox')} value={contact.dataBox} />
      </CardContent>
      <ContactForm personId={personId} contact={contact} open={open} onOpenChange={setOpen} />
    </Card>
  )
}
