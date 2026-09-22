import { Paper, Typography } from '@mui/material'

export function DashboardPage() {
    return (
        <>
            <Typography variant="h4" component="h1" gutterBottom>Dashboard</Typography>
            <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
                <Typography variant="h6" component="h2" gutterBottom>Bem-vindo ao ERP Gestão</Typography>
                <Typography color="text.secondary">
                    Utilize o menu de navegação para acessar as páginas disponíveis para o seu usuário.
                </Typography>
            </Paper>
        </>
    )
}
