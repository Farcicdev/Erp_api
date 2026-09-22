import { AppBar, Button, Toolbar, Typography } from '@mui/material'
import keycloak from '../auth/keycloak'

type TopbarProps = {
    menuAberto: boolean
    onAbrirMenu: () => void
}

export function Topbar({ menuAberto, onAbrirMenu }: TopbarProps) {
    const nomeUsuario = keycloak.tokenParsed?.name
        ?? keycloak.tokenParsed?.preferred_username
        ?? 'Usuário'

    function sair() {
        keycloak.logout({ redirectUri: window.location.origin })
    }

    return (
        <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
            <Toolbar sx={{ gap: 1 }}>
                <Button
                    color="inherit"
                    onClick={onAbrirMenu}
                    aria-label="Abrir menu de navegação"
                    aria-expanded={menuAberto}
                    sx={{ display: { xs: 'inline-flex', md: 'none' } }}
                >
                    Menu
                </Button>
                <Typography variant="h6" noWrap sx={{ flexGrow: 1, flexShrink: 0, fontSize: { xs: 16, sm: 20 } }}>ERP Gestão</Typography>
                <Typography sx={{ minWidth: 0, maxWidth: { xs: 120, sm: 280 } }} noWrap title={nomeUsuario}>
                    {nomeUsuario}
                </Typography>
                <Button color="inherit" onClick={sair}>Sair</Button>
            </Toolbar>
        </AppBar>
    )
}
