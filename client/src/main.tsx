import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { ReactKeycloakProvider } from "@react-keycloak/web";
import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
  // url: "http://localhost:8180/",
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: "is-stag",
  clientId: "React-client",
});

createRoot(document.getElementById('root')!).render(
    <ReactKeycloakProvider authClient={keycloak}>
      <App />
    </ReactKeycloakProvider>
)
