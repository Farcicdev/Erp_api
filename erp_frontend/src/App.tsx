import { Button, CssBaseline, ThemeProvider, Typography } from '@mui/material'
import { BrowserRouter } from 'react-router'
import './App.css'
import keycloak from './auth/keycloak'
import { AppRoutes } from './routes/AppRoutes'
import { theme } from './theme/theme'

function App() {
    function entrar() {
        keycloak.login()
    }

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <BrowserRouter>
                {keycloak.authenticated ? (
                    <AppRoutes />
                ) : (
                    <main className="pagina-inicial">
                        <Typography variant="h4" component="h1">ERP Gestão</Typography>
                        <p>Entre para acessar o sistema.</p>

                        <Button
                            variant="contained"
                            onClick={entrar}
                        >
                            Entrar
                        </Button>
                    </main>
                )}
            </BrowserRouter>
        </ThemeProvider>
    )
}

export default App
