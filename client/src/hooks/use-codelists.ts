import { useTranslation } from 'react-i18next'

import { $codelist } from '@/lib/api'

export function useCodelist(domain: string) {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'cs' | 'en'

  return $codelist.useQuery('get', '/domains/{domain}', {
    params: { path: { domain }, header: { 'Accept-Language': lang } },
  })
}

export function useCountries() {
  const { i18n } = useTranslation()
  const lang = i18n.language as 'cs' | 'en'

  return $codelist.useQuery('get', '/countries', {
    params: { header: { 'Accept-Language': lang } },
  })
}
