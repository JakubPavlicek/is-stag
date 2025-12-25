import { useTranslation } from 'react-i18next'

import { useForm } from '@tanstack/react-form'
import { useQueryClient } from '@tanstack/react-query'

import type { components } from '@/api/user/schema'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { FormField } from '@/components/ui/field-info'
import { Input } from '@/components/ui/input'
import { $user } from '@/lib/api'
import { contactSchema } from '@/lib/validations/user'

type Contact = components['schemas']['Contact']

interface ContactFormProps {
  personId: number
  contact: Contact
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ContactForm({ personId, contact, open, onOpenChange }: Readonly<ContactFormProps>) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const { mutateAsync } = $user.useMutation('patch', '/persons/{personId}', {
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: $user.queryOptions('get', '/persons/{personId}', {
          params: { path: { personId } },
        }).queryKey, // Simplification, need actual key construction or general invalidation
      })
      // Also invalidate contact specific query if it existed separately? No, contact is part of person.
      queryClient.invalidateQueries({
        queryKey: ['persons', personId], // Broad invalidation
      })
      onOpenChange(false)
      // toast.success(t('saved_successfully'))
    },
    onError: (error) => {
      console.error(error)
      // toast.error(t('error_occured'))
    },
  })

  const form = useForm({
    defaultValues: {
      email: contact.email ?? '',
      phone: contact.phone ?? '',
      mobile: contact.mobile ?? '',
      dataBox: contact.dataBox ?? '',
    },
    validators: {
      onChange: contactSchema,
    },
    onSubmit: async ({ value }) => {
      await mutateAsync({
        params: {
          path: { personId },
        },
        body: {
          contact: value,
        },
      })
    },
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-106.25">
        <DialogHeader>
          <DialogTitle>{t('my_data.contact.edit_title')}</DialogTitle>
          <DialogDescription>{t('my_data.contact.edit_description')}</DialogDescription>
        </DialogHeader>
        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            form.handleSubmit()
          }}
          className="grid gap-4 py-4"
        >
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <form.Field
              name="email"
            children={(field) => (
              <FormField field={field} label={t('my_data.contact.email')}>
                <Input
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </FormField>
            )}
          />
            <form.Field
              name="phone"
            children={(field) => (
              <FormField field={field} label={t('my_data.contact.phone')}>
                <Input
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </FormField>
            )}
          />
            <form.Field
              name="mobile"
            children={(field) => (
              <FormField field={field} label={t('my_data.contact.mobile')}>
                <Input
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </FormField>
            )}
          />
            <form.Field
              name="dataBox"
            children={(field) => (
              <FormField field={field} label={t('my_data.contact.databox')}>
                <Input
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </FormField>
            )}
          />
          </div>
          <DialogFooter>
            <form.Subscribe
              selector={(state) => [state.canSubmit, state.isSubmitting]}
              children={([canSubmit, isSubmitting]) => (
                <Button type="submit" disabled={!canSubmit}>
                  {isSubmitting ? t('saving') : t('save')}
                </Button>
              )}
            />
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
