import { useKeycloak } from '@react-keycloak/web'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/')({
  component: Index,
})

function Index() {
  const { keycloak } = useKeycloak()

  return (
    <div className="p-2">
      {keycloak.authenticated && (
        <div>
          <p>JWT: {keycloak.token}</p>
        </div>
      )}
    </div>
  )
}
