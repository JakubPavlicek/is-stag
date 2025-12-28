import { initReactI18next } from 'react-i18next'

import i18n from 'i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import Backend from 'i18next-http-backend'
import { z } from 'zod'
import { zodI18nMap } from 'zod-i18n-map'

/** Internationalization (i18n) configuration. */
i18n
  .use(Backend) // Load translations via HTTP (e.g., from /public/locales)
  .use(LanguageDetector) // Detect user language from browser settings, cookies, or local storage
  .use(initReactI18next) // Pass the i18n instance to react-i18next
  .init({
    fallbackLng: 'cs',
    supportedLngs: ['cs', 'en'],
    // Load only the language part (e.g. 'en') instead of language+region (e.g. 'en-US')
    load: 'languageOnly',
    // 'zod' namespace is used for validation error messages
    ns: ['translation', 'zod'],
    debug: import.meta.env.DEV,
    interpolation: {
      escapeValue: false,
    },
    backend: {
      loadPath: '/locales/{{lng}}/{{ns}}.json',
    },
  })

// Set the global error map for Zod to use i18next translations for validation errors
z.setErrorMap(zodI18nMap)

export { default as z } from 'zod'
export { default } from 'i18next'
