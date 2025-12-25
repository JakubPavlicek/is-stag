import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'

import { useForm } from '@tanstack/react-form'
import { useQueryClient } from '@tanstack/react-query'

import type { components } from '@/api/user/schema'
import { Button } from '@/components/ui/button'
import { Combobox } from '@/components/ui/combobox'
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
import { personalInfoSchema } from '@/lib/validations/user'

type Person = components['schemas']['PersonResponse']

interface PersonalInfoFormProps {
  person: Person
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PersonalInfoForm({ person, open, onOpenChange }: Readonly<PersonalInfoFormProps>) {
  const { t, i18n } = useTranslation()
  const queryClient = useQueryClient()
  const personId = person.personId
  const lang = (i18n.language?.split('-')[0] || 'cs') as 'cs' | 'en'

  const { data: titlesBefore } = $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain: 'TITUL_PRED' }, header: { 'Accept-Language': lang } },
  })

  const { data: titlesAfter } = $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain: 'TITUL_ZA' }, header: { 'Accept-Language': lang } },
  })

  const { data: maritalStatus } = $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain: 'STAV' }, header: { 'Accept-Language': lang } },
  })

  const { data: countries } = $codelist.useQuery('get', '/countries', {
    params: { header: { 'Accept-Language': lang } },
  })

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

  useEffect(() => {
    form.reset({
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
    })
  }, [person, form])

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
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <form.Field
              name="titles.prefix"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.title_before')}>
                  <Combobox
                    value={field.state.value}
                    onSelect={(value) => field.handleChange(value)}
                    options={
                      titlesBefore?.values.map((title) => ({
                        value: title.abbreviation ?? title.key,
                        label: title.abbreviation ?? title.key,
                      })) ?? []
                    }
                    placeholder={t('common.select_option')}
                    emptyText={t('common.no_option_found')}
                    modal
                  />
                </FormField>
              )}
            />
            <form.Field
              name="titles.suffix"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.title_after')}>
                  <Combobox
                    value={field.state.value}
                    onSelect={(value) => field.handleChange(value)}
                    options={
                      titlesAfter?.values.map((title) => ({
                        value: title.abbreviation ?? title.key,
                        label: title.abbreviation ?? title.key,
                      })) ?? []
                    }
                    placeholder={t('common.select_option')}
                    emptyText={t('common.no_option_found')}
                    modal
                  />
                </FormField>
              )}
            />
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
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
                  <Combobox
                    value={field.state.value}
                    onSelect={(value) => field.handleChange(value)}
                    options={
                      maritalStatus?.values.map((status) => ({
                        value: status.name,
                        label: status.name,
                      })) ?? []
                    }
                    placeholder={t('common.select_option')}
                    emptyText={t('common.no_option_found')}
                    modal
                  />
                </FormField>
              )}
            />
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <form.Field
              name="birthPlace.country"
              children={(field) => (
                <FormField field={field} label={t('my_data.personal_info.birth_place_country')}>
                  <Combobox
                    value={field.state.value}
                    onSelect={(value) => field.handleChange(value)}
                    options={
                      countries?.countries.map((country) => ({
                        value: country.name,
                        label: country.name,
                      })) ?? []
                    }
                    placeholder={t('common.select_option')}
                    emptyText={t('common.no_option_found')}
                    modal
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
