import { Alert, Button, CircularProgress, Paper, Stack, Typography } from '@mui/material'
import { Link } from 'react-router'
import keycloak from '../auth/keycloak'
import { useLoja } from '../contexts/useLoja'

export function DashboardPage() {
    const { lojaAtual, carregando, erro, recarregarLojas, possuiMultiplasLojas } = useLoja()
    const ehAdminCliente = keycloak.hasResourceRole('ADMIN_CLIENTE', 'erp-api')

    return (
        <>
            <Typography variant="h4" component="h1" gutterBottom>Dashboard</Typography>
            <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
                {!ehAdminCliente ? (
                    <>
                        <Typography variant="h6" component="h2" gutterBottom>Bem-vindo ao ERP Gestão</Typography>
                        <Typography color="text.secondary">
                            Utilize o menu de navegação para acessar as páginas disponíveis para o seu usuário.
                        </Typography>
                    </>
                ) : carregando ? (
                    <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }} role="status">
                        <CircularProgress size={24} aria-label="Carregando lojas" />
                        <Typography>Carregando suas lojas...</Typography>
                    </Stack>
                ) : erro ? (
                    <Alert severity="error" action={<Button color="inherit" onClick={() => void recarregarLojas()}>Tentar novamente</Button>}>
                        {erro}
                    </Alert>
                ) : lojaAtual ? (
                    <>
                        <Typography variant="h6" component="h2" gutterBottom>Bem-vindo à {lojaAtual.nomeFantasia}!</Typography>
                        <Typography>Loja atual: {lojaAtual.nomeFantasia}</Typography>
                    </>
                ) : possuiMultiplasLojas ? (
                    <Stack spacing={2} sx={{ alignItems: 'flex-start' }}>
                        <Typography>Selecione a loja que deseja acessar para continuar.</Typography>
                        <Button component={Link} to="/lojas" variant="contained">Selecionar loja</Button>
                    </Stack>
                ) : (
                    <Alert severity="info">Você não possui lojas disponíveis.</Alert>
                )}
            </Paper>
        </>
    )
}
