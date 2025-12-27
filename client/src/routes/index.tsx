import { useKeycloak } from '@react-keycloak/web'
import { createFileRoute } from '@tanstack/react-router'

import { DashboardSection } from '@/components/features/home/DashboardSection'
import { HeroSection } from '@/components/features/home/HeroSection'
import { PublicFeatures } from '@/components/features/home/PublicFeatures'

export const Route = createFileRoute('/')({
  component: Index,
})

function Index() {
  const { keycloak } = useKeycloak()

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
