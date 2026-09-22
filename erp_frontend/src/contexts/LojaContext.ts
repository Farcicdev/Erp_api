import { createContext } from 'react'
import type { Loja } from '../types/Loja'

type LojaContextValue = {
    lojas: Loja[]
    lojaAtual: Loja | null
    carregando: boolean
    erro: string
    selecionarLoja: (loja: Loja) => void
    recarregarLojas: () => Promise<void>
    possuiMultiplasLojas: boolean
}

export const LojaContext = createContext<LojaContextValue | undefined>(undefined)
