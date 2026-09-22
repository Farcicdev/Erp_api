import { unidadesComerciais } from '../types/Produto'
import type { ProdutoCadastro } from '../types/Produto'

export type ErrosProduto = Partial<Record<keyof ProdutoCadastro, string>>

export function validarProduto(dados: ProdutoCadastro): ErrosProduto {
    const erros: ErrosProduto = {}
    if (!dados.codigoInterno.trim()) erros.codigoInterno = 'Informe o código interno.'
    else if (dados.codigoInterno.length > 50) erros.codigoInterno = 'Use no máximo 50 caracteres.'
    if (!dados.descricao.trim()) erros.descricao = 'Informe a descrição.'
    else if (dados.descricao.length > 255) erros.descricao = 'Use no máximo 255 caracteres.'
    if (!unidadesComerciais.includes(dados.unidade)) erros.unidade = 'Selecione uma unidade válida.'
    if (!/^[0-9]{8}$/.test(dados.ncm)) erros.ncm = 'NCM deve possuir exatamente 8 números.'
    if (dados.gtin !== null && !/^(?:[0-9]{8}|[0-9]{12}|[0-9]{13}|[0-9]{14})$/.test(dados.gtin)) {
        erros.gtin = 'Código de barras deve possuir 8, 12, 13 ou 14 números.'
    }
    if (dados.cest !== null && !/^[0-9]{7}$/.test(dados.cest)) erros.cest = 'CEST deve possuir exatamente 7 números.'
    return erros
}
