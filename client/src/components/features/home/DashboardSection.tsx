import { useTranslation } from 'react-i18next'

import { Link } from '@tanstack/react-router'
import { ArrowRight, Bell, BookOpen, Calendar, User } from 'lucide-react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

interface DashboardSectionProps {
  userName: string
}

export function DashboardSection({ userName }: Readonly<DashboardSectionProps>) {
  const { t } = useTranslation()

  return (
    <section className="mx-auto w-full max-w-7xl px-6 py-12 lg:px-8">
      <div className="mb-10">
        <h2 className="text-3xl font-bold tracking-tight">
          {t('home.welcome_user', { name: userName })}
        </h2>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 xl:grid-cols-4">
        {/* My Data Card */}
        <Link to="/my-data" className="group">
          <Card className="hover:border-primary/50 hover:shadow-primary/5 h-full transition-all hover:-translate-y-1 hover:shadow-lg">
            <CardHeader>
              <div className="bg-primary/10 group-hover:bg-primary/20 mb-4 flex h-12 w-12 items-center justify-center rounded-lg transition-colors">
                <User className="text-primary h-6 w-6" />
              </div>
              <CardTitle>{t('home.cards.my_data')}</CardTitle>
              <CardDescription>{t('home.cards.my_data_desc')}</CardDescription>
            </CardHeader>
            <CardContent className="mt-auto pt-0">
              <div className="text-primary flex items-center text-sm font-medium">
                {t('common.open')} <ArrowRight className="ml-1 h-4 w-4" />
              </div>
            </CardContent>
          </Card>
        </Link>

        {/* Study Card */}
        <div className="group opacity-75 grayscale transition-all hover:opacity-100 hover:grayscale-0">
          <Card className="h-full">
            <CardHeader>
              <div className="bg-muted mb-4 flex h-12 w-12 items-center justify-center rounded-lg">
                <BookOpen className="text-muted-foreground h-6 w-6" />
              </div>
              <CardTitle className="flex items-center justify-between">
                {t('home.cards.study')}
                <span className="bg-muted text-muted-foreground rounded-full px-2 py-0.5 text-xs font-normal">
                  {t('common.coming_soon')}
                </span>
              </CardTitle>
              <CardDescription>{t('home.cards.study_desc')}</CardDescription>
            </CardHeader>
          </Card>
        </div>

        {/* Schedule Card */}
        <div className="group opacity-75 grayscale transition-all hover:opacity-100 hover:grayscale-0">
          <Card className="h-full">
            <CardHeader>
              <div className="bg-muted mb-4 flex h-12 w-12 items-center justify-center rounded-lg">
                <Calendar className="text-muted-foreground h-6 w-6" />
              </div>
              <CardTitle className="flex items-center justify-between">
                {t('home.cards.schedule')}
                <span className="bg-muted text-muted-foreground rounded-full px-2 py-0.5 text-xs font-normal">
                  {t('common.coming_soon')}
                </span>
              </CardTitle>
              <CardDescription>{t('home.cards.schedule_desc')}</CardDescription>
            </CardHeader>
          </Card>
        </div>

        {/* News Card */}
        <div className="group opacity-75 grayscale transition-all hover:opacity-100 hover:grayscale-0">
          <Card className="h-full">
            <CardHeader>
              <div className="bg-muted mb-4 flex h-12 w-12 items-center justify-center rounded-lg">
                <Bell className="text-muted-foreground h-6 w-6" />
              </div>
              <CardTitle className="flex items-center justify-between">
                {t('home.cards.news')}
                <span className="bg-muted text-muted-foreground rounded-full px-2 py-0.5 text-xs font-normal">
                  {t('common.coming_soon')}
                </span>
              </CardTitle>
              <CardDescription>{t('home.cards.news_desc')}</CardDescription>
            </CardHeader>
          </Card>
        </div>
      </div>
    </section>
  )
}
