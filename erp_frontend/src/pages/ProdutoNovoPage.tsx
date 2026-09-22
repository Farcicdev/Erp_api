import { useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { Alert, Box, Button, MenuItem, Paper, Stack, TextField, Typography } from '@mui/material'
import { useNavigate } from 'react-router'
import { criarProduto } from '../api/produtosApi'
import { useLoja } from '../contexts/useLoja'
import { unidadesComerciais } from '../types/Produto'
import type { ProdutoCadastro, UnidadeComercial } from '../types/Produto'
import { validarProduto } from '../validation/produtoCadastro'
import type { ErrosProduto } from '../validation/produtoCadastro'

export function ProdutoNovoPage() {
    const { lojaAtual } = useLoja()
    const navegar = useNavigate()
    const [campos, setCampos] = useState({ codigoInterno: '', descricao: '', gtin: '', unidade: '', ncm: '', cest: '' })
    const [erros, setErros] = useState<ErrosProduto>({})
    const [erro, setErro] = useState('')
    const [salvando, setSalvando] = useState(false)
    const envioEmAndamento = useRef(false)
    const montado = useRef(false)

    useEffect(() => {
        montado.current = true
        return () => { montado.current = false }
    }, [])

    function alterarCampo(campo: keyof ProdutoCadastro, valor: string) {
        if (campo === 'gtin' && /^[0-9]+$/.test(valor)) {
            // Remove os zeros de preenchimento antes de completar novamente.
            valor = valor.replace(/^0+(?=[0-9])/, '').padStart(13, '0')
        }
        setCampos((anteriores) => ({ ...anteriores, [campo]: valor }))
        setErros((anteriores) => ({ ...anteriores, [campo]: undefined }))
    }

    async function salvar(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault()
        if (!lojaAtual || envioEmAndamento.current) return
        // Mesma normalização do ProdutoRequest: espaços externos removidos e opcionais vazios como null.
        const dados: ProdutoCadastro = {
            codigoInterno: campos.codigoInterno.trim(),
            descricao: campos.descricao.trim(),
            gtin: campos.gtin.trim() || null,
            unidade: campos.unidade as UnidadeComercial,
            ncm: campos.ncm.trim(),
            cest: campos.cest.trim() || null,
        }
        const falhas = validarProduto(dados)
        setErros(falhas)
        setErro('')
        if (Object.keys(falhas).length) return

        envioEmAndamento.current = true
        setSalvando(true)
        try {
            await criarProduto(lojaAtual.id, dados)
            // Uma troca de loja desmonta este formulário; o retorno antigo não navega na nova loja.
            if (montado.current) navegar('/produtos', { replace: true })
        } catch (falha) {
            if (montado.current) setErro(falha instanceof Error ? falha.message.replace(/GTIN/g, 'Código de barras') : 'Não foi possível cadastrar o produto.')
        } finally {
            envioEmAndamento.current = false
            if (montado.current) setSalvando(false)
        }
    }

    return (
        <Stack spacing={2}>
            <Box>
                <Typography variant="h4" component="h1">Novo produto</Typography>
                <Typography color="text.secondary">Loja atual: {lojaAtual?.nomeFantasia}</Typography>
            </Box>
            <Paper component="form" noValidate onSubmit={salvar} variant="outlined" sx={{ p: { xs: 2, sm: 3 } }}>
                <Stack spacing={3}>
                    <Typography variant="body2">Campos marcados com * são obrigatórios.</Typography>
                    {erro && <Alert severity="error">{erro}</Alert>}
                    <Box sx={{ display: 'grid', gap: 3, gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' } }}>
                        <TextField label="Código interno" required fullWidth value={campos.codigoInterno}
                            onChange={(evento) => alterarCampo('codigoInterno', evento.target.value)} disabled={salvando}
                            error={!!erros.codigoInterno} helperText={erros.codigoInterno ?? 'Até 50 caracteres.'} />
                        <TextField label="Descrição" required fullWidth value={campos.descricao}
                            onChange={(evento) => alterarCampo('descricao', evento.target.value)} disabled={salvando}
                            error={!!erros.descricao} helperText={erros.descricao ?? 'Até 255 caracteres.'} />
                        <TextField label="Código de barras" fullWidth value={campos.gtin}
                            onChange={(evento) => alterarCampo('gtin', evento.target.value)} disabled={salvando}
                            slotProps={{ htmlInput: { inputMode: 'numeric' } }}
                            error={!!erros.gtin} helperText={erros.gtin ?? 'Opcional. Completado com zeros à esquerda até 13 dígitos.'} />
                        <TextField label="Unidade" select required fullWidth value={campos.unidade}
                            onChange={(evento) => alterarCampo('unidade', evento.target.value)} disabled={salvando}
                            error={!!erros.unidade} helperText={erros.unidade ?? 'Selecione a unidade comercial.'}>
                            {unidadesComerciais.map((unidade) => <MenuItem key={unidade} value={unidade}>{unidade}</MenuItem>)}
                        </TextField>
                        <TextField label="NCM" required fullWidth value={campos.ncm}
                            onChange={(evento) => alterarCampo('ncm', evento.target.value)} disabled={salvando}
                            slotProps={{ htmlInput: { inputMode: 'numeric' } }}
                            error={!!erros.ncm} helperText={erros.ncm ?? 'Exatamente 8 números.'} />
                        <TextField label="CEST" fullWidth value={campos.cest}
                            onChange={(evento) => alterarCampo('cest', evento.target.value)} disabled={salvando}
                            slotProps={{ htmlInput: { inputMode: 'numeric' } }}
                            error={!!erros.cest} helperText={erros.cest ?? 'Opcional: exatamente 7 números.'} />
                    </Box>
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                        <Button type="submit" variant="contained" disabled={salvando}>{salvando ? 'Salvando...' : 'Salvar produto'}</Button>
                        <Button type="button" disabled={salvando} onClick={() => navegar('/produtos')}>Cancelar</Button>
                    </Stack>
                </Stack>
            </Paper>
        </Stack>
    )
}
