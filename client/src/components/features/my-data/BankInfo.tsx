import type { ElementType } from 'react'
import { useState } from 'react'
import { useTranslation } from 'react-i18next'

import { CreditCard, Edit, Euro, User } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { BankInfoForm } from '@/components/features/my-data/forms/BankInfoForm'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type Banking = components['schemas']['BankAccountsResponse']
type Account = components['schemas']['BankAccount']

/**
 * Renders a single bank account block (standard or Euro account).
 * - Shows placeholder UI if an account is null/undefined.
 * - Displays account number, bank details, IBAN, and holder information.
 * - Optionally allows editing via the provided `onEdit` callback.
 */
function AccountBlock({
  title,
  account,
  icon: Icon,
  onEdit,
}: Readonly<{
  title: string
  account?: Account | null
  icon: ElementType
  onEdit?: () => void
}>) {
  const { t } = useTranslation()

  // Show empty state with edit button if no account data exists
  if (!account) {
    return (
      <div className="text-muted-foreground/50 flex flex-col gap-2 rounded-xl border border-dashed p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Icon className="h-4 w-4" />
            <span className="text-sm font-medium">{title}</span>
          </div>
          {onEdit && (
            <Button variant="ghost" size="icon" onClick={onEdit} className="h-8 w-8">
              <Edit className="h-3 w-3" />
            </Button>
          )}
        </div>
        <p className="pl-6 text-sm">{t('my_data.bank.not_provided')}</p>
      </div>
    )
  }

  // Construct the full account number by joining prefix and suffix (if they exist)
  const fullAccount = [account.accountNumberPrefix, account.accountNumberSuffix]
    .filter(Boolean)
    .join('-')

  return (
    <div className="bg-card/50 hover:bg-accent/20 flex flex-col gap-4 rounded-xl border p-4 transition-all hover:shadow-md">
      <div className="flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <div className="text-primary flex items-center gap-2">
            <Icon className="h-4 w-4" />
            <h4 className="text-foreground text-sm font-semibold tracking-tight">{title}</h4>
          </div>
          {onEdit && (
            <Button variant="ghost" size="icon" onClick={onEdit} className="-mt-2 -mr-2 h-8 w-8">
              <Edit className="h-4 w-4" />
            </Button>
          )}
        </div>

        <div className="flex flex-col gap-1">
          <div className="text-primary flex flex-wrap items-baseline gap-x-1 text-xl font-bold tracking-tight">
            <span>
              {fullAccount || '-'}/{account.bankCode || '-'}
            </span>
          </div>
          <p className="text-muted-foreground text-sm font-medium">{account.bankName || '-'}</p>
        </div>
      </div>

      <div className="grid gap-3 pt-2">
        {account.iban && (
          <div className="bg-background/50 flex items-center gap-2 rounded-md border px-3 py-2 text-sm">
            <span className="text-muted-foreground text-xs font-bold tracking-wider uppercase">
              {t('my_data.bank.iban')}
            </span>
            <span className="font-mono font-medium">{account.iban}</span>
          </div>
        )}

        <div className="bg-background/50 flex items-start gap-3 rounded-lg border p-3">
          <div className="bg-muted flex h-8 w-8 shrink-0 items-center justify-center rounded-full">
            <User className="text-muted-foreground h-4 w-4" />
          </div>
          <div className="grid gap-0.5">
            <p className="text-muted-foreground text-xs font-bold tracking-wider uppercase">
              {t('my_data.bank.owner')}
            </p>
            <p className="text-sm font-medium">{account.holderName || '-'}</p>
            {account.holderAddress && (
              <p className="text-muted-foreground text-xs">{account.holderAddress}</p>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

/**
 * Displays user's banking information (Standard and Euro accounts).
 * - Allows editing of the standard account via `BankInfoForm`.
 * - Shows account details like number, bank code, IBAN, and holder info.
 */
export function BankInfo({ banking, personId }: Readonly<{ banking: Banking; personId: number }>) {
  const { t } = useTranslation()
  const [open, setOpen] = useState(false)

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('my_data.bank.title')}</CardTitle>
      </CardHeader>
      <CardContent className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <AccountBlock
          title={t('my_data.bank.account_standard')}
          account={banking.account}
          icon={CreditCard}
          onEdit={() => setOpen(true)}
        />
        <AccountBlock
          title={t('my_data.bank.account_euro')}
          account={banking.euroAccount}
          icon={Euro}
        />
      </CardContent>
      <BankInfoForm
        personId={personId}
        account={banking.account}
        open={open}
        onOpenChange={setOpen}
      />
    </Card>
  )
}
