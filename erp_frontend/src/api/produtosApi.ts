import type { PageResponse } from '../types/PageResponse'
import type { Produto, ProdutoCadastro } from '../types/Produto'
import { apiClient } from './apiClient'

export async function listarProdutos(lojaId: number, page: number, size: number, ativo = true): Promise<PageResponse<Produto>> {
    const resposta = await apiClient<PageResponse<Produto>>(`/api/lojas/${lojaId}/produtos?page=${page}&size=${size}&ativo=${ativo}`)
    if (!resposta) throw new Error('O servidor não retornou a página de produtos. Tente novamente.')
    return resposta
}

export async function mudarStatusProduto(lojaId: number, produtoId: number, ativo: boolean): Promise<void> {
    await apiClient(`/api/lojas/${lojaId}/produtos/${produtoId}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ ativo }),
    })
}

export async function criarProduto(lojaId: number, dados: ProdutoCadastro): Promise<Produto> {
    const resposta = await apiClient<Produto>(`/api/lojas/${lojaId}/produtos`, {
        method: 'POST',
        body: JSON.stringify(dados),
    })
    if (!resposta) throw new Error('O servidor não retornou o produto. Confira a listagem antes de tentar novamente.')
    return resposta
}

export async function buscarProdutos(lojaId: number, busca: string, page: number, size: number, ativo: boolean): Promise<PageResponse<Produto>> {
    const parametros = new URLSearchParams({ busca, page: String(page), size: String(size), ativo: String(ativo), sort: 'id,asc' })
    const resposta = await apiClient<PageResponse<Produto>>(`/api/lojas/${lojaId}/produtos/buscar?${parametros}`)
    if (!resposta) throw new Error('O servidor não retornou a página de produtos. Tente novamente.')
    return resposta
}

export async function buscarProdutoPorId(lojaId: number, produtoId: number): Promise<Produto> {
    const resposta = await apiClient<Produto>(`/api/lojas/${lojaId}/produtos/${produtoId}`)
    if (!resposta || resposta.lojaId !== lojaId || resposta.id !== produtoId) {
        throw new Error('Produto não disponível na loja selecionada.')
    }
    return resposta
}

export async function alterarProduto(lojaId: number, produtoId: number, dados: ProdutoCadastro): Promise<Produto> {
    const resposta = await apiClient<Produto>(`/api/lojas/${lojaId}/produtos/${produtoId}/alterar`, {
        method: 'PATCH', body: JSON.stringify(dados),
    })
    if (!resposta || resposta.lojaId !== lojaId || resposta.id !== produtoId) {
        throw new Error('Não foi possível confirmar a alteração. Atualize a listagem antes de tentar novamente.')
    }
    return resposta
}
