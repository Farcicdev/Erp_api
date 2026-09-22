import { Button } from '@mui/material'
import './App.css'
import keycloak from './auth/keycloak'
import { LojasPage } from './pages/LojasPage'

function App() {
    const nomeUsuario =
        keycloak.tokenParsed?.preferred_username ?? 'Usuário'

    const roles =
        keycloak.tokenParsed?.resource_access?.['erp-api']?.roles ?? []

    function entrar() {
        keycloak.login()
    }

    function sair() {
        keycloak.logout({
            redirectUri: window.location.origin,
        })
    }

    return (
        <main className="pagina-inicial">
            <h1>ERP Gestão</h1>

            {keycloak.authenticated ? (
                <>
                    <h2>Olá, {nomeUsuario}</h2>

                    <p>Roles: {roles.join(', ')}</p>

                    <LojasPage />

                    <Button
                        variant="outlined"
                        color="error"
                        onClick={sair}
                    >
                        Sair
                    </Button>
                </>
            ) : (
                <>
                    <p>Entre para acessar o sistema.</p>

                    <Button
                        variant="contained"
                        onClick={entrar}
                    >
                        Entrar
                    </Button>
                </>
            )}
        </main>
    )
}

export default App