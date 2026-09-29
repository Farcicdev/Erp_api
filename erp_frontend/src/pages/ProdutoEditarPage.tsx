import { useEffect, useState } from 'react'
import { Alert, Button, CircularProgress, Stack, Typography } from '@mui/material'
import { Link, useParams } from 'react-router'
import { buscarProdutoPorId } from '../api/produtosApi'
import { ProdutoForm } from '../components/ProdutoForm'
import { useLoja } from '../contexts/useLoja'
import type { Produto } from '../types/Produto'

type Resultado = { chave: string, produto?: Produto, erro?: string }

export function ProdutoEditarPage() {
    const { produtoId } = useParams()
    const { lojaAtual } = useLoja()
    const lojaId = lojaAtual?.id
    const id = Number(produtoId)
    const [tentativa, setTentativa] = useState(0)
    const [resultado, setResultado] = useState<Resultado | null>(null)
    const chave = `${lojaId}:${produtoId}:${tentativa}`
    const valido = Number.isSafeInteger(id) && id > 0

    useEffect(() => {
        if (lojaId === undefined || !valido) return
        let ignorar = false
        buscarProdutoPorId(lojaId, id).then((produto) => {
            if (!ignorar) setResultado({ chave, produto })
        }).catch((falha: unknown) => {
            if (!ignorar) setResultado({ chave, erro: falha instanceof Error
                ? falha.message.replace(/gtin/gi, 'Código de barras') : 'Não foi possível carregar o produto.' })
        })
        return () => { ignorar = true }
    }, [lojaId, id, valido, chave])

    if (!valido) return <Alert severity="error">Identificador de produto inválido. <Link to="/produtos">Voltar aos produtos</Link></Alert>
    if (resultado?.chave !== chave) return (
        <Stack direction="row" spacing={2} role="status">
            <CircularProgress size={24} aria-label="Carregando produto" />
            <Typography>Carregando produto...</Typography>
        </Stack>
    )
    if (resultado.erro || !resultado.produto?.ativo) return (
        <Stack spacing={2}>
            <Alert severity="error">{resultado.erro || 'Reative o produto antes de editar.'}</Alert>
            <Typography>Produtos inativos precisam ser reativados na listagem antes da edição.</Typography>
            <Stack direction="row" spacing={2}>
                <Button onClick={() => setTentativa((valor) => valor + 1)}>Tentar novamente</Button>
                <Button component={Link} to="/produtos">Voltar aos produtos</Button>
            </Stack>
        </Stack>
    )
    return <ProdutoForm key={`${lojaId}:${id}`} produto={resultado.produto} />
}
