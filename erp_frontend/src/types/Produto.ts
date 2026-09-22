export const unidadesComerciais = ['UN', 'CX', 'PCT', 'PAR', 'DZ', 'KG', 'G', 'L', 'ML', 'M', 'M2', 'M3'] as const

export type UnidadeComercial = typeof unidadesComerciais[number]

export type ProdutoCadastro = {
    codigoInterno: string
    descricao: string
    gtin: string | null
    unidade: UnidadeComercial
    ncm: string
    cest: string | null
}

export type Produto = ProdutoCadastro & {
    id: number
    ativo: boolean
    lojaId: number
}
