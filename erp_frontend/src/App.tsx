import './App.css'
import keycloak from './auth/keycloak.ts'

function App(){
    function entrar(){
        keycloak.login()
    }

    function sair(){
        keycloak.logout({
            redirectUri: window.location.origin
        })
    }

    const nomeUsuario = keycloak.tokenParsed?.preferred_username ?? 'Usuario'

    const roles = keycloak.tokenParsed?.resource_access?.['erp-api']?.roles ?? []

    return(
        <main className="pagina-inicial">
            <h1>ERP Gestao</h1>
            {keycloak.authenticated ?(
            <>
                <h2>Olá, {nomeUsuario}</h2>

                <p>Roles: {roles.length > 0 ? roles.join(',') : 'Nenhuma role encontrada' }</p>

                <button onClick={sair}>Sair</button>
            </>
            ) : (
                <>
                <p>Entre para Acessar o sistema</p>

                <button onClick={entrar}>
                    Entrar
                </button>
                </>
            )}
        </main>
        )
}

export default App