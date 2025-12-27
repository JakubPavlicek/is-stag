import { useTranslation } from 'react-i18next'

import { useKeycloak } from '@react-keycloak/web'
import { GraduationCap, LogIn } from 'lucide-react'

import { Button } from '@/components/ui/button'

export function HeroSection() {
  const { t } = useTranslation()
  const { keycloak } = useKeycloak()

  return (
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
  )
}
