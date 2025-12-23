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
import { bankAccountSchema } from '@/lib/validations/user'

type Account = components['schemas']['BankAccount']

interface BankInfoFormProps {
  personId: number
  account?: Account | null
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function BankInfoForm({
  personId,
  account,
  open,
  onOpenChange,
}: Readonly<BankInfoFormProps>) {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const { mutateAsync } = $user.useMutation('patch', '/persons/{personId}', {
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['persons', personId], // Should probably also invalidate '/persons/{personId}/banking' but that depends on implementation details. 'persons' key covers all usually? No, I should invalidate banking specific key.
      })
      // Invalidate banking specifically
      queryClient.invalidateQueries({
        queryKey: $user.queryOptions('get', '/persons/{personId}/banking', {
          params: { path: { personId } },
        }).queryKey,
      })
      onOpenChange(false)
    },
    onError: (error) => {
      console.error(error)
    },
  })

  const form = useForm({
    defaultValues: {
      accountNumberPrefix: account?.accountNumberPrefix ?? '',
      accountNumberSuffix: account?.accountNumberSuffix ?? '',
      bankCode: account?.bankCode ?? '',
      holderName: account?.holderName ?? '',
      holderAddress: account?.holderAddress ?? '',
    },
    validators: {
      onChange: bankAccountSchema,
    },
    onSubmit: async ({ value }) => {
      await mutateAsync({
        params: {
          path: { personId },
        },
        body: {
          bankAccount: value,
        },
      })
    },
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-125">
        <DialogHeader>
          <DialogTitle>{t('my_data.bank.edit_title')}</DialogTitle>
          <DialogDescription>{t('my_data.bank.edit_description')}</DialogDescription>
        </DialogHeader>
        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            form.handleSubmit()
          }}
          className="grid gap-4 py-4"
        >
          <div className="grid grid-cols-3 gap-4">
            <div className="col-span-1">
              <form.Field
                name="accountNumberPrefix"
                children={(field) => (
                  <FormField field={field} label={t('my_data.bank.account_prefix')}>
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
            <div className="col-span-2">
              <form.Field
                name="accountNumberSuffix"
                children={(field) => (
                  <FormField field={field} label={t('my_data.bank.account_suffix')}>
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
          </div>

          <form.Field
            name="bankCode"
            children={(field) => (
              <FormField field={field} label={t('my_data.bank.bank_code')}>
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
            name="holderName"
            children={(field) => (
              <FormField field={field} label={t('my_data.bank.holder_name')}>
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
            name="holderAddress"
            children={(field) => (
              <FormField field={field} label={t('my_data.bank.holder_address')}>
                <Input
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </FormField>
            )}
          />

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
