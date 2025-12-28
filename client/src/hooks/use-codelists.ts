import { useTranslation } from 'react-i18next'

import { $codelist } from '@/lib/api'

/**
 * Fetches codelist values for a specific domain.
 * - Automatically includes the current language in the Accept-Language header.
 */
export function useCodelist(domain: string) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'cs' | 'en'

  return $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain }, header: { 'Accept-Language': lang } },
  })
}

/**
 * Fetches the list of countries.
 * - Automatically includes the current language in the Accept-Language header.
 */
export function useCountries() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'cs' | 'en'

  return $codelist.useQuery('get', '/countries', {
    params: { header: { 'Accept-Language': lang } },
  })
}
