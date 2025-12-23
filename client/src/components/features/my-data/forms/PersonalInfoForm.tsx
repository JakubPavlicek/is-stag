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
import { personalInfoSchema } from '@/lib/validations/user'

type Person = components['schemas']['PersonResponse']

interface PersonalInfoFormProps {
  person: Person
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PersonalInfoForm({ person, open, onOpenChange }: Readonly<PersonalInfoFormProps>) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const personId = person.personId

  const { mutateAsync } = $user.useMutation('patch', '/persons/{personId}', {
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['persons', personId],
      })
      onOpenChange(false)
    },
    onError: (error) => {
      console.error(error)
    },
  })

  const form = useForm({
    defaultValues: {
      titles: {
        prefix: person.titles?.prefix ?? '',
        suffix: person.titles?.suffix ?? '',
      },
      birthSurname: person.birthSurname ?? '',
      maritalStatus: person.maritalStatus ?? '',
      birthPlace: {
        country: person.birthPlace?.country ?? '',
        city: person.birthPlace?.city ?? '',
      },
    },
    validators: {
      onChange: personalInfoSchema,
    },
    onSubmit: async ({ value }) => {
      await mutateAsync({
        params: {
          path: { personId },
        },
        body: {
          titles: value.titles,
          birthSurname: value.birthSurname,
          maritalStatus: value.maritalStatus,
          birthPlace: value.birthPlace,
        },
      })
    },
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-150">
        <DialogHeader>
          <DialogTitle>{t('my_data.personal_info.edit_title')}</DialogTitle>
          <DialogDescription>{t('my_data.personal_info.edit_description')}</DialogDescription>
        </DialogHeader>
        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            form.handleSubmit()
          }}
          className="grid gap-4 py-4"
        >
          <div className="grid grid-cols-2 gap-4">
            <form.Field
              name="titles.prefix"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.title_before')}>
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
              name="titles.suffix"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.title_after')}>
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

          <div className="grid grid-cols-2 gap-4">
            <form.Field
              name="birthSurname"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.birth_surname')}>
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
              name="maritalStatus"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.marital_status')}>
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

          <div className="grid grid-cols-2 gap-4">
            <form.Field
              name="birthPlace.country"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.birth_place_country')}>
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
              name="birthPlace.city"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.birth_place_city')}>
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
