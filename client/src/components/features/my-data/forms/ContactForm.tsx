import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'

import { useForm } from '@tanstack/react-form'

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
import { useFormSubmit } from '@/hooks/use-form-submit'
import { $user } from '@/lib/api'
import { contactSchema } from '@/lib/validations/user'

type Contact = components['schemas']['Contact']

interface ContactFormProps {
  personId: number
  contact: Contact
  open: boolean
  onOpenChange: (open: boolean) => void
}

/**
 * Modal form for editing user contact information (email, phone, etc.).
 * - Validates inputs with `contactSchema`.
 * - Handles success/error feedback.
 */
export function ContactForm({ personId, contact, open, onOpenChange }: Readonly<ContactFormProps>) {
  const { t } = useTranslation()
  const { handleSubmit } = useFormSubmit()

  const { mutateAsync } = $user.useMutation('patch', '/persons/{personId}')

  // Initialize the form with TanStack Form
  const form = useForm({
    // Map initial API data to form state fields
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
      // Use helper to handle submission state, error catching, and success notifications
      await handleSubmit(
        {
          params: { path: { personId } },
          body: {
            contact: {
              // Convert empty strings back to null for the API if necessary.
              email: value.email || null,
              phone: value.phone || null,
              mobile: value.mobile || null,
              dataBox: value.dataBox || null,
            },
          },
        },
        {
          mutationFn: mutateAsync,
          // Invalidate the user data query to refresh the UI immediately
          invalidateKeys: [['get', '/persons/{personId}']],
          onSuccess: () => onOpenChange(false),
        },
      )
    },
  })

  // Reset form values whenever the modal opens to ensure we don't show stale state
  // if the user closed the modal without saving previously modified data.
  useEffect(() => {
    if (open) {
      form.reset({
        email: contact.email ?? '',
        phone: contact.phone ?? '',
        mobile: contact.mobile ?? '',
        dataBox: contact.dataBox ?? '',
      })
    }
  }, [open, contact, form])

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-150">
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
          <div className="grid grid-cols-1 items-start gap-4 md:grid-cols-2">
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
