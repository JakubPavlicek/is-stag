import { Outlet, createRootRoute } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/router-devtools'

import { Header } from '@/components/layout/Header'
import { Sidebar } from '@/components/layout/Sidebar'

export const Route = createRootRoute({
  component: () => (
    <div className="flex min-h-screen flex-col md:flex-row">
      <div className="hidden md:block">
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
  ),
})
