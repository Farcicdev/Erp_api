import { useState } from 'react'
import {
    Alert,
    Button,
    List,
    ListItem,
    ListItemText,
    Typography,
} from '@mui/material'
import keycloak from '../auth/keycloak'
import type { Loja } from '../types/Loja'

export function LojasPage() {
    const [lojas, setLojas] = useState<Loja[]>([])
    const [carregando, setCarregando] = useState(false)
    const [erro, setErro] = useState('')
    const [buscaRealizada, setBuscaRealizada] = useState(false)

    async function buscarLojas() {
        setCarregando(true)
        setErro('')

        try {
            await keycloak.updateToken(30)

            if (!keycloak.token) {
                throw new Error('Token não encontrado')
            }

            const resposta = await fetch('/api/me/lojas', {
                headers: {
                    Authorization: `Bearer ${keycloak.token}`,
                },
            })

            if (!resposta.ok) {
                throw new Error(`Erro HTTP: ${resposta.status}`)
            }

            const dados: Loja[] = await resposta.json()

            setLojas(dados)
            setBuscaRealizada(true)
        } catch (erro) {
            console.error(erro)
            setErro('Não foi possível carregar as lojas.')
        } finally {
            setCarregando(false)
        }
    }

    return (
        <section>
            <Typography variant="h5">
                Minhas lojas
            </Typography>

            <Button
                variant="contained"
                onClick={buscarLojas}
                disabled={carregando}
            >
                {carregando ? 'Carregando...' : 'Buscar lojas'}
            </Button>

            {erro && (
                <Alert severity="error">
                    {erro}
                </Alert>
            )}

            {buscaRealizada && lojas.length === 0 && (
                <Alert severity="info">
                    Nenhuma loja encontrada.
                </Alert>
            )}

            <List>
                {lojas.map((loja) => (
                    <ListItem key={loja.id}>
                        <ListItemText
                            primary={loja.nome}
                            secondary={loja.nomeFantasia}
                        />
                    </ListItem>
                ))}
            </List>
        </section>
    )
}