import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { ReactKeycloakProvider } from "@react-keycloak/web";
import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: "React-client",
});

createRoot(document.getElementById('root')!).render(
    <ReactKeycloakProvider authClient={keycloak}>
      <App />
    </ReactKeycloakProvider>
)
