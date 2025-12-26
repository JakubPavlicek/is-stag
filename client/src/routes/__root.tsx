import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'

import { QueryClient } from '@tanstack/react-query'
import { Outlet, createRootRouteWithContext, useRouter } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'

import { Header } from '@/components/layout/Header'
import { Sidebar } from '@/components/layout/Sidebar'

interface MyRouterContext {
  queryClient: QueryClient
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: RootComponent,
})

function RootComponent() {
  const { i18n } = useTranslation()
  const router = useRouter()

  useEffect(() => {
    router.invalidate()
  }, [i18n.language, router])

  return (
    <div className="flex min-h-screen flex-col lg:flex-row">
      <div className="hidden lg:block">
        <Sidebar />
      </div>
      <div className="flex flex-1 flex-col">
        <Header />
        <main className="flex-1">
          <Outlet />
        </main>
      </div>
      <TanStackRouterDevtools />
    </div>
  )
}

