import keycloak from '../auth/keycloak'

export async function apiClient<T>(caminho: string, opcoes: RequestInit = {}): Promise<T | undefined> {
    try {
        await keycloak.updateToken(30)
    } catch {
        throw new Error('Não foi possível renovar sua sessão. Entre novamente no sistema.')
    }

    if (!keycloak.token) {
        throw new Error('Sua sessão não está disponível. Entre novamente no sistema.')
    }

    const headers = new Headers(opcoes.headers)
    headers.set('Authorization', `Bearer ${keycloak.token}`)
    headers.set('Accept', 'application/json')

    let resposta: Response
    try {
        resposta = await fetch(caminho, { ...opcoes, headers })
    } catch {
        throw new Error('Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.')
    }

    const mensagens: Record<number, string> = {
        401: 'Sua sessão expirou ou não é válida. Entre novamente no sistema.',
        403: 'Você não tem permissão para acessar este recurso.',
        404: 'O recurso solicitado não foi encontrado.',
        409: 'A operação entrou em conflito com os dados atuais. Atualize os dados e tente novamente.',
    }

    if (!resposta.ok) {
        throw new Error(mensagens[resposta.status]
            ?? `Não foi possível concluir a solicitação (HTTP ${resposta.status}). Tente novamente.`)
    }

    try {
        const corpo = await resposta.text()
        // Inclui respostas 204 e outras respostas bem-sucedidas sem conteúdo.
        return corpo.trim() ? JSON.parse(corpo) as T : undefined
    } catch {
        throw new Error('Não foi possível ler a resposta do servidor. Tente novamente.')
    }
}
