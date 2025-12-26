import { useTranslation } from 'react-i18next'

import { useKeycloak } from '@react-keycloak/web'
import { Link, createFileRoute } from '@tanstack/react-router'
import { ArrowRight, Bell, BookOpen, Calendar, GraduationCap, LogIn, User } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

export const Route = createFileRoute('/')({
  component: Index,
})

function Index() {
  const { keycloak } = useKeycloak()
  const { t } = useTranslation()

  const userName =
    keycloak.tokenParsed?.given_name || keycloak.tokenParsed?.preferred_username || 'Student'

  return (
    <div className="flex min-h-[calc(100vh-4rem)] flex-col">
      {/* Hero Section */}
      <section
        className={`relative overflow-hidden px-6 py-24 sm:py-32 lg:px-8 ${
          keycloak.authenticated ? '' : 'flex-1'
        }`}
      >
        {/* Modern Gradient Background */}
        <div className="bg-background absolute inset-0 -z-10 h-full w-full">
          <div className="bg-background absolute top-0 z-[-2] h-screen w-screen bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,hsl(var(--primary)/0.15),rgba(255,255,255,0))]"></div>
        </div>

        <div className="relative z-10 mx-auto max-w-2xl text-center">
          <div className="bg-primary/10 text-primary ring-primary/20 mx-auto mb-8 flex h-16 w-16 items-center justify-center rounded-2xl ring-1">
            <GraduationCap size={32} />
          </div>
          <h1 className="text-foreground text-4xl font-bold tracking-tight sm:text-6xl">
            {t('home.title')}
          </h1>
          <p className="text-muted-foreground mt-6 text-lg leading-8">{t('home.subtitle')}</p>

          {!keycloak.authenticated && (
            <div className="mt-10 flex flex-col items-center justify-center gap-4">
              <Button
                onClick={() => keycloak.login()}
                size="lg"
                className="shadow-primary/20 hover:shadow-primary/30 h-12 rounded-full px-8 text-lg shadow-lg transition-all hover:scale-105"
              >
                <LogIn className="mr-2 h-5 w-5" />
                {t('login')}
              </Button>
              <p className="text-muted-foreground text-sm">{t('home.login_description')}</p>
            </div>
          )}
        </div>
      </section>

      {/* Dashboard Section (Authenticated) */}
      {keycloak.authenticated && (
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

            {/* Study Card (Placeholder) */}
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

            {/* Schedule Card (Placeholder) */}
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

            {/* News Card (Placeholder) */}
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
      )}

      {/* Footer / Features Grid for Public (Logged Out) */}
      {!keycloak.authenticated && (
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
      )}
    </div>
  )
}
