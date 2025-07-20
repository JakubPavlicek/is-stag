import './App.css'
import { useKeycloak } from "@react-keycloak/web";

function App() {
  const { keycloak } = useKeycloak();

  return (
    <>
      {
        !keycloak.authenticated && (
          <button onClick={() => keycloak.login({
            redirectUri: window.location.origin,
          })}>LOGIN</button>
        )
      }
      {
        keycloak.authenticated && (
          <div>
            <p>Welcome user: {keycloak.token}</p>
            <button onClick={() => keycloak.logout()}>LOGOUT</button>
          </div>
        )
      }
    </>
  )
}

export default App
