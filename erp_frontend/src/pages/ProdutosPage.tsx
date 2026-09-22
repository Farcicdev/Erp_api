import { useEffect, useState } from 'react'
import {
    Alert, Box, Button, Chip, CircularProgress, Paper, Stack, Table, TableBody,
    TableCell, TableContainer, TableHead, TablePagination, TableRow, Typography,
} from '@mui/material'
import { Link } from 'react-router'
import { listarProdutos } from '../api/produtosApi'
import { useLoja } from '../contexts/useLoja'
import type { PageResponse } from '../types/PageResponse'
import type { Produto } from '../types/Produto'

type Resultado = { chave: string, dados: PageResponse<Produto> | null, erro: string }

export function ProdutosPage() {
    const { lojaAtual } = useLoja()
    const lojaId = lojaAtual?.id
    const [pagina, setPagina] = useState(0)
    const [tamanho, setTamanho] = useState(10)
    const [tentativa, setTentativa] = useState(0)
    const [resultado, setResultado] = useState<Resultado | null>(null)
    const chave = `${lojaId}:${pagina}:${tamanho}:${tentativa}`
    // Dados só são exibidos quando pertencem à consulta atual.
    const carregando = resultado?.chave !== chave
    const dados = !carregando ? resultado?.dados : null
    const erro = !carregando ? resultado?.erro : ''

    useEffect(() => {
        if (lojaId === undefined) return
        let ignorar = false
        async function buscar() {
            try {
                const resposta = await listarProdutos(lojaId!, pagina, tamanho)
                if (!ignorar) setResultado({ chave, dados: resposta, erro: '' })
            } catch (falha) {
                if (!ignorar) setResultado({
                    chave, dados: null,
                    erro: falha instanceof Error ? falha.message : 'Não foi possível carregar os produtos.',
                })
            }
        }
        void buscar()
        // Descarta respostas que chegam após troca de loja, página ou saída da tela.
        return () => { ignorar = true }
    }, [lojaId, pagina, tamanho, chave])

    return (
        <Stack spacing={2}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ justifyContent: 'space-between' }}>
                <Box>
                    <Typography variant="h4" component="h1">Produtos</Typography>
                    <Typography color="text.secondary">Loja atual: {lojaAtual?.nomeFantasia}</Typography>
                </Box>
                <Button component={Link} to="/produtos/novo" variant="contained" sx={{ alignSelf: 'flex-start' }}>Novo produto</Button>
            </Stack>
            {carregando ? (
                <Stack direction="row" spacing={2} role="status" sx={{ alignItems: 'center' }}>
                    <CircularProgress size={24} aria-label="Carregando produtos" />
                    <Typography>Carregando produtos...</Typography>
                </Stack>
            ) : erro ? (
                <Alert severity="error" action={<Button color="inherit" onClick={() => setTentativa((valor) => valor + 1)}>Tentar novamente</Button>}>
                    {erro}
                </Alert>
            ) : dados && (
                <Paper variant="outlined" sx={{ minWidth: 0 }}>
                    {dados.empty ? (
                        <Alert severity="info">Nenhum produto encontrado nesta página da loja.</Alert>
                    ) : (
                        <TableContainer>
                            <Table sx={{ minWidth: 760 }} aria-label={`Produtos de ${lojaAtual?.nomeFantasia}`}>
                                <TableHead>
                                    <TableRow>
                                        {['Código interno', 'Descrição', 'Código de barras', 'Unidade', 'NCM', 'CEST', 'Situação'].map((coluna) => (
                                            <TableCell key={coluna}>{coluna}</TableCell>
                                        ))}
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {dados.content.map((produto) => (
                                        <TableRow key={produto.id}>
                                            <TableCell sx={{ overflowWrap: 'anywhere', maxWidth: 180 }}>{produto.codigoInterno}</TableCell>
                                            <TableCell sx={{ overflowWrap: 'anywhere', maxWidth: 360 }}>{produto.descricao}</TableCell>
                                            <TableCell>{produto.gtin ?? '—'}</TableCell>
                                            <TableCell>{produto.unidade}</TableCell>
                                            <TableCell>{produto.ncm}</TableCell>
                                            <TableCell>{produto.cest ?? '—'}</TableCell>
                                            <TableCell><Chip size="small" label={produto.ativo ? 'Ativo' : 'Inativo'} color={produto.ativo ? 'success' : 'default'} /></TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                    <TablePagination
                        component="div"
                        count={dados.totalElements}
                        page={pagina}
                        rowsPerPage={tamanho}
                        rowsPerPageOptions={[10, 25, 50, 100]}
                        onPageChange={(_, novaPagina) => setPagina(novaPagina)}
                        onRowsPerPageChange={(evento) => { setTamanho(Number(evento.target.value)); setPagina(0) }}
                        labelRowsPerPage="Por página:"
                        labelDisplayedRows={({ from, to, count }) => `${from}–${to} de ${count}`}
                        getItemAriaLabel={(tipo) => ({ first: 'Primeira página', last: 'Última página', next: 'Próxima página', previous: 'Página anterior' })[tipo]}
                        sx={{ '& .MuiTablePagination-toolbar': { flexWrap: 'wrap', justifyContent: 'flex-end', px: 1 }, '& .MuiTablePagination-spacer': { display: 'none' } }}
                    />
                </Paper>
            )}
        </Stack>
    )
}
