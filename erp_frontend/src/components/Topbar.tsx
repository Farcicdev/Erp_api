import { AppBar, Box, Button, Toolbar, Typography } from '@mui/material'
import { Link } from 'react-router'
import keycloak from '../auth/keycloak'
import { useLoja } from '../contexts/useLoja'

type TopbarProps = {
    menuAberto: boolean
    onAbrirMenu: () => void
}

export function Topbar({ menuAberto, onAbrirMenu }: TopbarProps) {
    const { lojaAtual, possuiMultiplasLojas } = useLoja()
    const ehAdminCliente = keycloak.hasResourceRole('ADMIN_CLIENTE', 'erp-api')
    const nomeUsuario = keycloak.tokenParsed?.name
        ?? keycloak.tokenParsed?.preferred_username
        ?? 'Usuário'

    function sair() {
        keycloak.logout({ redirectUri: window.location.origin })
    }

    return (
        <AppBar position="static">
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
            {ehAdminCliente && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, px: { xs: 2, sm: 3 }, pb: 1 }}>
                    <Typography noWrap sx={{ flex: 1, minWidth: 0 }} title={lojaAtual?.nomeFantasia}>
                        {lojaAtual?.nomeFantasia ?? 'Nenhuma loja selecionada'}
                    </Typography>
                    {possuiMultiplasLojas && (
                        <Button color="inherit" component={Link} to="/lojas" sx={{ flexShrink: 0 }}>Trocar loja</Button>
                    )}
                </Box>
            )}
        </AppBar>
    )
}
