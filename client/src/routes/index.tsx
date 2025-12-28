import { useKeycloak } from '@react-keycloak/web'
import { createFileRoute } from '@tanstack/react-router'

import { DashboardSection } from '@/components/features/home/DashboardSection'
import { HeroSection } from '@/components/features/home/HeroSection'
import { PublicFeatures } from '@/components/features/home/PublicFeatures'

export const Route = createFileRoute('/')({
  component: Index,
})

/**
 * The homepage route component.
 * - Always shows the `HeroSection`.
 * - If authenticated: shows `DashboardSection` with user content.
 * - If not authenticated: shows `PublicFeatures` marketing content.
 */
function Index() {
  const { keycloak } = useKeycloak()

  // Extract the user's first name or fallback to username or "Student"
  const userName =
    keycloak.tokenParsed?.given_name || keycloak.tokenParsed?.preferred_username || 'Student'

  return (
    <div className="flex min-h-[calc(100vh-4rem)] flex-col">
      <HeroSection />
      {keycloak.authenticated && <DashboardSection userName={userName} />}
      {!keycloak.authenticated && <PublicFeatures />}
    </div>
  )
}
