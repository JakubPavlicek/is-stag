import { useTranslation } from 'react-i18next'

import { Calendar, GraduationCap, User } from 'lucide-react'

export function PublicFeatures() {
  const { t } = useTranslation()

  return (
    <section className="bg-muted/30 py-16">
      <div className="mx-auto max-w-7xl px-6 lg:px-8">
        <div className="grid max-w-2xl grid-cols-1 gap-x-8 gap-y-16 sm:grid-cols-2 lg:mx-0 lg:max-w-none lg:grid-cols-3">
          <div className="flex flex-col items-start">
            <div className="bg-primary/10 ring-primary/20 rounded-xl p-3 ring-1">
              <User className="text-primary h-6 w-6" aria-hidden="true" />
            </div>
            <div className="text-foreground mt-4 font-semibold">
              {t('home.public.portal_title')}
            </div>
            <div className="text-muted-foreground mt-2 leading-7">
              {t('home.public.portal_desc')}
            </div>
          </div>
          <div className="flex flex-col items-start">
            <div className="bg-primary/10 ring-primary/20 rounded-xl p-3 ring-1">
              <Calendar className="text-primary h-6 w-6" aria-hidden="true" />
            </div>
            <div className="text-foreground mt-4 font-semibold">
              {t('home.public.scheduling_title')}
            </div>
            <div className="text-muted-foreground mt-2 leading-7">
              {t('home.public.scheduling_desc')}
            </div>
          </div>
          <div className="flex flex-col items-start">
            <div className="bg-primary/10 ring-primary/20 rounded-xl p-3 ring-1">
              <GraduationCap className="text-primary h-6 w-6" aria-hidden="true" />
            </div>
            <div className="text-foreground mt-4 font-semibold">
              {t('home.public.academics_title')}
            </div>
            <div className="text-muted-foreground mt-2 leading-7">
              {t('home.public.academics_desc')}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
