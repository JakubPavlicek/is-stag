import type { ElementType } from 'react'
import { useTranslation } from 'react-i18next'

import { CreditCard, Euro, User } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

type Banking = components['schemas']['BankAccountsResponse']
type Account = components['schemas']['BankAccount']

function AccountBlock({
  title,
  account,
  icon: Icon,
}: Readonly<{ title: string; account?: Account | null; icon: ElementType }>) {
  const { t } = useTranslation()

  if (!account) {
    return (
      <div className="text-muted-foreground/50 flex flex-col gap-2 rounded-xl border border-dashed p-4">
        <div className="flex items-center gap-2">
          <Icon className="h-4 w-4" />
          <span className="text-sm font-medium">{title}</span>
        </div>
        <p className="pl-6 text-sm">{t('my_data.bank.not_provided')}</p>
      </div>
    )
  }

  const fullAccount = [account.accountNumberPrefix, account.accountNumberSuffix]
    .filter(Boolean)
    .join('-')

  return (
    <div className="bg-card/50 hover:bg-accent/20 flex flex-col gap-4 rounded-xl border p-4 transition-all hover:shadow-md">
      <div className="flex items-start justify-between gap-4">
        <div className="text-primary flex items-center gap-2">
          <Icon className="h-4 w-4" />
          <h4 className="text-foreground text-sm font-semibold tracking-tight">{title}</h4>
        </div>
        <div className="text-right">
          <p className="text-primary text-lg font-bold tracking-tight">
            {fullAccount || '-'} / {account.bankCode || '-'}
          </p>
          <p className="text-muted-foreground text-xs font-medium">{account.bankName || '-'}</p>
        </div>
      </div>

      <div className="grid gap-3">
        {account.iban && (
          <div className="bg-muted/50 flex items-center gap-2 rounded-md px-3 py-2 text-sm">
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

export function BankInfo({ banking }: Readonly<{ banking: Banking }>) {
  const { t } = useTranslation()

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
        />
        <AccountBlock
          title={t('my_data.bank.account_euro')}
          account={banking.euroAccount}
          icon={Euro}
        />
      </CardContent>
    </Card>
  )
}
