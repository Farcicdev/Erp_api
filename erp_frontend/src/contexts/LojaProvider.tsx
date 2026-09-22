import { useCallback, useEffect, useRef, useState } from 'react'
import type { ReactNode } from 'react'
import { listarMinhasLojas } from '../api/lojasApi'
import keycloak from '../auth/keycloak'
import type { Loja } from '../types/Loja'
import { LojaContext } from './LojaContext'

const chaveLojaAtual = 'erp.lojaAtualId'

function lerIdSalvo(): string | null {
    try {
        return localStorage.getItem(chaveLojaAtual)
    } catch {
        return null
    }
}

function salvarId(loja: Loja | null) {
    try {
        if (loja) {
            localStorage.setItem(chaveLojaAtual, String(loja.id))
        } else {
            localStorage.removeItem(chaveLojaAtual)
        }
    } catch {
        // Se o navegador bloquear o armazenamento, a seleção funciona apenas em memória.
    }
}

export function LojaProvider({ children }: { children: ReactNode }) {
    const ehAdminCliente = keycloak.hasResourceRole('ADMIN_CLIENTE', 'erp-api')
    const [lojas, setLojas] = useState<Loja[]>([])
    const [lojaAtual, setLojaAtual] = useState<Loja | null>(null)
    const [carregando, setCarregando] = useState(ehAdminCliente)
    const [erro, setErro] = useState('')
    const montado = useRef(false)
    const requisicao = useRef<Promise<void> | null>(null)
    const idAtual = useRef<number | null>(null)

    const recarregarLojas = useCallback((): Promise<void> => {
        if (!ehAdminCliente) return Promise.resolve()
        // O segundo efeito do StrictMode reutiliza a mesma requisição em andamento.
        if (requisicao.current) return requisicao.current

        const carregar = async () => {
            await Promise.resolve()
            if (!montado.current) return
            setCarregando(true)
            setErro('')
            setLojaAtual(null)

            try {
                const resposta = await listarMinhasLojas()
                if (!montado.current) return

                const ativas = resposta.filter((loja) => loja.ativo)
                const idSalvo = idAtual.current === null ? lerIdSalvo() : String(idAtual.current)
                const selecionada = ativas.length === 1
                    ? ativas[0]
                    : ativas.find((loja) => String(loja.id) === idSalvo) ?? null

                setLojas(resposta)
                setLojaAtual(selecionada)
                idAtual.current = selecionada?.id ?? null
                // IDs ausentes ou inativos são descartados; nunca concedem acesso.
                salvarId(selecionada)
            } catch (falha) {
                if (!montado.current) return
                setLojas([])
                setErro(falha instanceof Error ? falha.message : 'Não foi possível carregar as lojas.')
            } finally {
                if (montado.current) setCarregando(false)
            }
        }

        requisicao.current = carregar().finally(() => { requisicao.current = null })
        return requisicao.current
    }, [ehAdminCliente])

    useEffect(() => {
        montado.current = true
        void recarregarLojas()
        return () => { montado.current = false }
    }, [recarregarLojas])

    function selecionarLoja(loja: Loja) {
        if (!ehAdminCliente || carregando || erro) return
        // Usa o objeto recebido da API, não o objeto fornecido pelo componente.
        const disponivel = lojas.find((item) => item.id === loja.id && item.ativo)
        if (!disponivel) return
        setLojaAtual(disponivel)
        idAtual.current = disponivel.id
        salvarId(disponivel)
    }

    return (
        <LojaContext.Provider value={{
            lojas, lojaAtual, carregando, erro, selecionarLoja, recarregarLojas,
            possuiMultiplasLojas: lojas.filter((loja) => loja.ativo).length > 1,
        }}>
            {children}
        </LojaContext.Provider>
    )
}
