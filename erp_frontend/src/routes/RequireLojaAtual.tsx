import { CircularProgress, Stack, Typography } from '@mui/material'
import { Navigate, Outlet } from 'react-router'
import { useLoja } from '../contexts/useLoja'

export function RequireLojaAtual() {
    const { lojaAtual, carregando, possuiMultiplasLojas } = useLoja()

    if (carregando) {
        return (
            <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }} role="status">
                <CircularProgress size={24} aria-label="Carregando lojas" />
                <Typography>Carregando suas lojas...</Typography>
            </Stack>
        )
    }
    if (!lojaAtual) {
        return <Navigate to={possuiMultiplasLojas ? '/lojas' : '/dashboard'} replace />
    }
    // Uma nova loja monta novamente as páginas: limpa produtos, paginação e formulário.
    return <Outlet key={lojaAtual.id} />
}
