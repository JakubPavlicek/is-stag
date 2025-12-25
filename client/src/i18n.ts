import { initReactI18next } from 'react-i18next'

import i18n from 'i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import Backend from 'i18next-http-backend'
import { z } from 'zod'
import { zodI18nMap } from 'zod-i18n-map'

i18n
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: 'cs',
    debug: import.meta.env.DEV,

    interpolation: {
      escapeValue: false,
    },
    backend: {
      loadPath: '/locales/{{lng}}/{{ns}}.json',
    },
  })

z.setErrorMap(zodI18nMap)

export { default as z } from 'zod'
export { default } from 'i18next'
