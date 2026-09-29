import { useEffect, useRef, useState } from 'react'
import {
    Alert, Box, Button, Chip, CircularProgress, Paper, Stack, Table, TableBody,
    TableCell, TableContainer, TableHead, TablePagination, TableRow, Typography, MenuItem, TextField,
    Dialog, DialogTitle, DialogContent, DialogContentText, DialogActions,
} from '@mui/material'
import { Link, useLocation, useNavigate } from 'react-router'
import { buscarProdutos, mudarStatusProduto } from '../api/produtosApi'
import { useLoja } from '../contexts/useLoja'
import type { PageResponse } from '../types/PageResponse'
import type { Produto } from '../types/Produto'

type Resultado = { chave: string, dados: PageResponse<Produto> | null, erro: string }

export function ProdutosPage() {
    const { lojaAtual } = useLoja()
    const lojaId = lojaAtual?.id
    const location = useLocation()
    const navegar = useNavigate()
    const [sucesso, setSucesso] = useState(() => location.state?.lojaId === lojaId
        && typeof location.state?.sucesso === 'string' ? location.state.sucesso as string : '')
    const [textoBusca, setTextoBusca] = useState('')
    const [busca, setBusca] = useState('')
    const [confirmacao, setConfirmacao] = useState<Produto | null>(null)
    const montado = useRef(false)
    const envioEmAndamento = useRef(false)
    const [pagina, setPagina] = useState(0)
    const [tamanho, setTamanho] = useState(10)
    const [tentativa, setTentativa] = useState(0)
    const [ativo, setAtivo] = useState(true)
    const [alterando, setAlterando] = useState(false)
    const [erroStatus, setErroStatus] = useState('')
    const [resultado, setResultado] = useState<Resultado | null>(null)
    const chave = JSON.stringify([lojaId, pagina, tamanho, tentativa, ativo, busca])
    // Dados só são exibidos quando pertencem à consulta atual.
    const carregando = resultado?.chave !== chave
    const dados = !carregando ? resultado?.dados : null
    const erro = !carregando ? resultado?.erro : ''

    useEffect(() => {
        montado.current = true
        return () => { montado.current = false }
    }, [])

    useEffect(() => {
        if (location.state?.sucesso) navegar(location.pathname, { replace: true, state: null })
    }, [location.state, location.pathname, navegar])

    useEffect(() => {
        if (lojaId === undefined) return
        let ignorar = false
        async function buscar() {
            try {
                const resposta = await buscarProdutos(lojaId!, busca, pagina, tamanho, ativo)
                if (!ignorar) setResultado({ chave, dados: resposta, erro: '' })
            } catch (falha) {
                if (!ignorar) setResultado({
                    chave, dados: null,
                    erro: falha instanceof Error ? falha.message.replace(/gtin/gi, 'Código de barras') : 'Não foi possível carregar os produtos.',
                })
            }
        }
        void buscar()
        // Descarta respostas que chegam após troca de loja, página ou saída da tela.
        return () => { ignorar = true }
    }, [lojaId, pagina, tamanho, chave, ativo, busca])

    async function alterarStatus(produto: Produto) {
        if (lojaId === undefined || produto.lojaId !== lojaId || envioEmAndamento.current) return
        envioEmAndamento.current = true
        setAlterando(true)
        setErroStatus('')
        setSucesso('')
        try {
            await mudarStatusProduto(lojaId, produto.id, !produto.ativo)
            if (!montado.current) return
            setConfirmacao(null)
            setSucesso(produto.ativo ? 'Produto inativado com sucesso.' : 'Produto reativado com sucesso.')
            setPagina(0)
            setTentativa((valor) => valor + 1)
        } catch (falha) {
            if (montado.current) setErroStatus(falha instanceof Error ? falha.message.replace(/gtin/gi, 'Código de barras') : 'Não foi possível alterar a situação.')
        } finally {
            envioEmAndamento.current = false
            if (montado.current) setAlterando(false)
        }
    }

    return (
        <Stack spacing={2}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ justifyContent: 'space-between' }}>
                <Box>
                    <Typography variant="h4" component="h1">Produtos</Typography>
                    <Typography color="text.secondary">Loja atual: {lojaAtual?.nomeFantasia}</Typography>
                </Box>
                <Button component={Link} to="/produtos/novo" variant="contained" sx={{ alignSelf: 'flex-start' }}>Novo produto</Button>
            </Stack>
            <Stack component="form" direction={{ xs: 'column', sm: 'row' }} spacing={2}
                onSubmit={(evento) => {
                    evento.preventDefault()
                    setBusca(textoBusca.trim()); setPagina(0); setTentativa((valor) => valor + 1)
                }}>
                <TextField label="Buscar produtos" value={textoBusca} fullWidth disabled={alterando}
                    onChange={(evento) => setTextoBusca(evento.target.value)}
                    helperText="Descrição, código interno ou Código de barras. Para códigos, informe o valor completo." />
                <Button type="submit" variant="outlined" disabled={alterando}>Buscar</Button>
                <Button disabled={alterando || (!textoBusca && !busca)} onClick={() => {
                    setTextoBusca(''); setBusca(''); setPagina(0)
                }}>Limpar</Button>
            </Stack>
            <TextField select label="Situação" value={String(ativo)} disabled={alterando}
                onChange={(evento) => { setAtivo(evento.target.value === 'true'); setPagina(0); setErroStatus('') }}
                sx={{ width: 180 }}>
                <MenuItem value="true">Ativos</MenuItem>
                <MenuItem value="false">Inativos</MenuItem>
            </TextField>
            {sucesso && <Alert severity="success" onClose={() => setSucesso('')}>{sucesso}</Alert>}
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
                        <Alert severity="info">{busca
                            ? 'Nenhum produto encontrado para esta busca e situação na loja selecionada.'
                            : `Nenhum produto ${ativo ? 'ativo' : 'inativo'} encontrado nesta página da loja.`}</Alert>
                    ) : (
                        <TableContainer>
                            <Table sx={{ minWidth: 760 }} aria-label={`Produtos de ${lojaAtual?.nomeFantasia}`}>
                                <TableHead>
                                    <TableRow>
                                        {['Código interno', 'Descrição', 'Código de barras', 'Unidade', 'NCM', 'CEST', 'Situação', 'Ações'].map((coluna) => (
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
                                            <TableCell>
                                                <Button component={Link} to={`/produtos/${produto.id}/editar`}
                                                    disabled={alterando || !produto.ativo}
                                                    title={!produto.ativo ? 'Reative o produto antes de editar.' : undefined}>Editar</Button>
                                                <Button disabled={alterando} onClick={() => { setErroStatus(''); setConfirmacao(produto) }}>
                                                {produto.ativo ? 'Inativar' : 'Reativar'}
                                            </Button></TableCell>
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
            <Dialog open={confirmacao !== null} onClose={() => { if (!alterando) setConfirmacao(null) }}
                aria-labelledby="confirmar-status-titulo" aria-describedby="confirmar-status-descricao">
                <DialogTitle id="confirmar-status-titulo">{confirmacao?.ativo ? 'Inativar produto' : 'Reativar produto'}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="confirmar-status-descricao">
                        Deseja {confirmacao?.ativo ? 'inativar' : 'reativar'} o produto “{confirmacao?.descricao}”
                        {' '}({confirmacao?.codigoInterno}) na loja {lojaAtual?.nomeFantasia}?
                    </DialogContentText>
                    {erroStatus && <Alert severity="error" sx={{ mt: 2 }}>{erroStatus}</Alert>}
                </DialogContent>
                <DialogActions>
                    <Button disabled={alterando} onClick={() => setConfirmacao(null)}>Cancelar</Button>
                    <Button variant="contained" disabled={alterando} onClick={() => { if (confirmacao) void alterarStatus(confirmacao) }}>
                        {alterando ? 'Salvando...' : 'Confirmar'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Stack>
    )
}
