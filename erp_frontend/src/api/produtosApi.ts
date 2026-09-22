import type { PageResponse } from '../types/PageResponse'
import type { Produto, ProdutoCadastro } from '../types/Produto'
import { apiClient } from './apiClient'

export async function listarProdutos(lojaId: number, page: number, size: number): Promise<PageResponse<Produto>> {
    const resposta = await apiClient<PageResponse<Produto>>(`/api/lojas/${lojaId}/produtos?page=${page}&size=${size}`)
    if (!resposta) throw new Error('O servidor não retornou a página de produtos. Tente novamente.')
    return resposta
}

export async function criarProduto(lojaId: number, dados: ProdutoCadastro): Promise<Produto> {
    const resposta = await apiClient<Produto>(`/api/lojas/${lojaId}/produtos`, {
        method: 'POST',
        body: JSON.stringify(dados),
    })
    if (!resposta) throw new Error('O servidor não retornou o produto. Confira a listagem antes de tentar novamente.')
    return resposta
}
