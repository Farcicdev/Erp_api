import { Alert, Box, Button, Card, CardActions, CardContent, Chip, CircularProgress, Stack, Typography } from '@mui/material'
import { useNavigate } from 'react-router'
import { useLoja } from '../contexts/useLoja'
import type { Loja } from '../types/Loja'

export function LojasPage() {
    const { lojas, lojaAtual, carregando, erro, selecionarLoja, recarregarLojas, possuiMultiplasLojas } = useLoja()
    const navegar = useNavigate()

    function escolherLoja(loja: Loja) {
        selecionarLoja(loja)
        navegar('/dashboard')
    }

    return (
        <Stack component="section" spacing={2}>
            <Typography variant="h4" component="h1">Minhas lojas</Typography>
            {carregando ? (
                <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }} role="status">
                    <CircularProgress size={24} aria-label="Carregando lojas" />
                    <Typography>Carregando suas lojas...</Typography>
                </Stack>
            ) : erro ? (
                <Alert severity="error" action={<Button color="inherit" onClick={() => void recarregarLojas()}>Tentar novamente</Button>}>
                    {erro}
                </Alert>
            ) : (
                <>
                    {!lojas.some((loja) => loja.ativo) && (
                        <Alert severity="info">Você não possui lojas disponíveis.</Alert>
                    )}
                    {lojaAtual && !possuiMultiplasLojas && (
                        <Alert severity="success">Sua única loja disponível foi selecionada automaticamente: {lojaAtual.nomeFantasia}.</Alert>
                    )}
                    {possuiMultiplasLojas && (
                        <Typography>Escolha a loja que deseja acessar.</Typography>
                    )}
                    <Box sx={{ display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', md: 'repeat(2, minmax(0, 1fr))', xl: 'repeat(3, minmax(0, 1fr))' } }}>
                        {lojas.map((loja) => {
                            const atual = loja.id === lojaAtual?.id
                            return (
                                <Card key={loja.id} variant="outlined" sx={{ display: 'flex', flexDirection: 'column', borderWidth: 2, borderColor: atual ? 'primary.main' : 'divider', overflowWrap: 'anywhere' }}>
                                    <CardContent sx={{ flexGrow: 1 }}>
                                        <Stack spacing={1}>
                                            <Typography variant="h6" component="h2">{loja.nomeFantasia}</Typography>
                                            {atual && <Chip label="Loja atual" color="primary" sx={{ alignSelf: 'flex-start' }} />}
                                            <Typography>Razão social: {loja.razaoSocial || 'Não informada'}</Typography>
                                            <Typography>CNPJ: {loja.cnpj || 'Não informado'}</Typography>
                                            <Typography>Inscrição estadual: {loja.inscricaoEstadual || 'Não informada'}</Typography>
                                            <Typography>Regime tributário: {loja.regimeTributario || 'Não informado'}</Typography>
                                            <Typography>Situação: {loja.ativo ? 'Ativa' : 'Inativa'}</Typography>
                                        </Stack>
                                    </CardContent>
                                    <CardActions>
                                        {atual ? (
                                            <Button onClick={() => navegar('/dashboard')}>Ir para o dashboard</Button>
                                        ) : (
                                            <Button variant="contained" disabled={!loja.ativo} onClick={() => escolherLoja(loja)}>
                                                Selecionar loja
                                            </Button>
                                        )}
                                    </CardActions>
                                </Card>
                            )
                        })}
                    </Box>
                </>
            )}
        </Stack>
    )
}
