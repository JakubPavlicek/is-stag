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
import { $codelist, $user } from '@/lib/api'
import { cn } from '@/lib/utils'
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

  const { data: titlesBefore } = $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain: 'TITUL_PRED' } },
  })

  const { data: titlesAfter } = $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain: 'TITUL_ZA' } },
  })

  const { data: maritalStatus } = $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain: 'STAV' } },
  })

  const { data: countries } = $codelist.useQuery('get', '/countries')

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
                  <select
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    className={cn(
                      'border-input file:text-foreground placeholder:text-muted-foreground focus-visible:ring-ring flex h-9 w-full rounded-md border bg-transparent px-3 py-1 text-base shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:ring-1 focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm',
                    )}
                  >
                    <option value="" />
                    {titlesBefore?.values.map((title) => (
                      <option key={title.key} value={title.abbreviation ?? title.key}>
                        {title.abbreviation}
                      </option>
                    ))}
                  </select>
                </FormField>
              )}
            />
            <form.Field
              name="titles.suffix"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.title_after')}>
                  <select
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    className={cn(
                      'border-input file:text-foreground placeholder:text-muted-foreground focus-visible:ring-ring flex h-9 w-full rounded-md border bg-transparent px-3 py-1 text-base shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:ring-1 focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm',
                    )}
                  >
                    <option value="" />
                    {titlesAfter?.values.map((title) => (
                      <option key={title.key} value={title.abbreviation ?? title.key}>
                        {title.abbreviation}
                      </option>
                    ))}
                  </select>
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
                  <select
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    className={cn(
                      'border-input file:text-foreground placeholder:text-muted-foreground focus-visible:ring-ring flex h-9 w-full rounded-md border bg-transparent px-3 py-1 text-base shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:ring-1 focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm',
                    )}
                  >
                    <option value="" />
                    {maritalStatus?.values.map((status) => (
                      <option key={status.key} value={status.name}>
                        {status.name}
                      </option>
                    ))}
                  </select>
                </FormField>
              )}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <form.Field
              name="birthPlace.country"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.birth_place_country')}>
                  <select
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    className={cn(
                      'border-input file:text-foreground placeholder:text-muted-foreground focus-visible:ring-ring flex h-9 w-full rounded-md border bg-transparent px-3 py-1 text-base shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:ring-1 focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm',
                    )}
                  >
                    <option value="" />
                    {countries?.countries.map((country) => (
                      <option key={country.id} value={country.name}>
                        {country.name}
                      </option>
                    ))}
                  </select>
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
