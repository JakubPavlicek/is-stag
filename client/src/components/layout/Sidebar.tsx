import { useTranslation } from 'react-i18next'

import { Link, useRouterState } from '@tanstack/react-router'
import { GraduationCap, Home, User } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

/**
 * The main sidebar navigation component.
 * - Displays the application logo.
 * - Provides navigation links (Home, My Data).
 * - Highlights the active route.
 * - Supports an `onNavigate` callback for mobile menu closing.
 */
export function Sidebar({
  className,
  onNavigate,
}: Readonly<{ className?: string; onNavigate?: () => void }>) {
  const { t } = useTranslation()
  const router = useRouterState()

  // Helper to check if a route is active
  const isPathActive = (path: string) => router.location.pathname === path

  return (
    <div className={cn('bg-card h-full w-60 border-r pb-12 shadow-sm', className)}>
      <div className="space-y-6 py-6">
        <div className="flex items-center gap-2 px-6">
          <div className="bg-primary/10 flex h-8 w-8 items-center justify-center rounded-lg">
            <GraduationCap className="text-primary h-5 w-5" />
          </div>
          <h2 className="text-lg font-bold tracking-tight">IS/STAG</h2>
        </div>

        <div className="space-y-1 px-3">
          <Link to="/" className="block" onClick={onNavigate}>
            <Button
              variant={isPathActive('/') ? 'secondary' : 'ghost'}
              className={cn(
                'w-full justify-start',
                isPathActive('/') ? 'bg-secondary font-semibold shadow-sm' : 'text-foreground',
              )}
            >
              <Home className={cn('mr-2 h-4 w-4', isPathActive('/') && 'text-primary')} />
              {t('sidebar.home')}
            </Button>
          </Link>
          <Link to="/my-data" className="block" onClick={onNavigate}>
            <Button
              variant={isPathActive('/my-data') ? 'secondary' : 'ghost'}
              className={cn(
                'w-full justify-start',
                isPathActive('/my-data')
                  ? 'bg-secondary font-semibold shadow-sm'
                  : 'text-foreground',
              )}
            >
              <User className={cn('mr-2 h-4 w-4', isPathActive('/my-data') && 'text-primary')} />
              {t('sidebar.my_data')}
            </Button>
          </Link>
        </div>
      </div>
    </div>
  )
}
