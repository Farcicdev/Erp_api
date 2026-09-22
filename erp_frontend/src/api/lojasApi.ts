import type { Loja } from '../types/Loja'
import { apiClient } from './apiClient'

export async function listarMinhasLojas(): Promise<Loja[]> {
    const lojas = await apiClient<Loja[]>('/api/me/lojas')
    return lojas ?? []
}
