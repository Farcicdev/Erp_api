import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import keycloak from "./auth/keycloak.ts";

keycloak.init({
    onLoad: 'check-sso',
    pkceMethod: 'S256',
}).then(() => {
    createRoot(document.getElementById('root')!).render(
        <StrictMode>
            <App />
        </StrictMode>
    )
}).catch((erro) => {
    console.error('Erro ao inicializar o Keycloak', erro)
})
