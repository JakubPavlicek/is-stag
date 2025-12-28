import { useTranslation } from 'react-i18next'

import { MapPin } from 'lucide-react'

import type { components } from '@/api/user/schema'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { cn } from '@/lib/utils'

type Addresses = components['schemas']['AddressesResponse']
type Address = components['schemas']['Address']

/**
 * Renders a single address block (permanent or temporary).
 * - Shows placeholder UI if address is null/undefined.
 * - Formats address parts into readable lines.
 */
function AddressBlock({
  title,
  address,
  className,
}: Readonly<{ title: string; address?: Address | null; className?: string }>) {
  const { t } = useTranslation()

  // Show an empty state with a dashed border if no address data is available
  if (!address) {
    return (
      <div
        className={cn(
          'text-muted-foreground/50 flex flex-col gap-2 rounded-xl border border-dashed p-4',
          className,
        )}
      >
        <div className="flex items-center gap-2">
          <MapPin className="h-4 w-4" />
          <span className="text-sm font-medium">{title}</span>
        </div>
        <p className="pl-6 text-sm">{t('my_data.addresses.not_provided')}</p>
      </div>
    )
  }

  // Format address parts into display-ready strings, filtering out nulls/empty values
  const streetLine = [address.street, address.streetNumber].filter(Boolean).join(' ')
  const cityLine = [address.zipCode, address.municipality].filter(Boolean).join(' ')
  const metaLine = [address.district, address.country].filter(Boolean).join(' • ')

  return (
    <div
      className={cn(
        'bg-card/50 hover:bg-accent/20 flex flex-col gap-3 rounded-xl border p-4 transition-all hover:shadow-md',
        className,
      )}
    >
      <div className="text-primary flex items-center gap-2">
        <MapPin className="h-4 w-4" />
        <h4 className="text-foreground text-sm font-semibold tracking-tight">{title}</h4>
      </div>

      <div className="space-y-1 pl-6">
        <p className="text-base leading-none font-medium">
          {streetLine || (
            <span className="text-muted-foreground font-normal italic">
              {t('my_data.addresses.no_street')}
            </span>
          )}
        </p>

        {address.municipalityPart && address.municipalityPart !== address.municipality && (
          <p className="text-muted-foreground text-sm">{address.municipalityPart}</p>
        )}

        <p className="text-muted-foreground text-sm">
          {cityLine || (
            <span className="italic opacity-50">{t('my_data.addresses.not_provided')}</span>
          )}
        </p>

        {metaLine && (
          <p className="text-muted-foreground/60 pt-1 text-xs font-medium tracking-wider uppercase">
            {metaLine}
          </p>
        )}
      </div>
    </div>
  )
}

/**
 * Displays user's address information.
 * - Shows Permanent and Temporary addresses side-by-side.
 * - Handles missing address data gracefully with visual feedback.
 */
export function AddressInfo({ addresses }: Readonly<{ addresses: Addresses }>) {
  const { t } = useTranslation()

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('my_data.addresses.title')}</CardTitle>
      </CardHeader>
      <CardContent className="grid grid-cols-1 gap-4 lg:grid-cols-2">
        <AddressBlock
          title={t('my_data.addresses.permanent')}
          address={addresses.permanentAddress}
        />
        <AddressBlock
          title={t('my_data.addresses.temporary')}
          address={addresses.temporaryAddress}
        />
      </CardContent>
    </Card>
  )
}
