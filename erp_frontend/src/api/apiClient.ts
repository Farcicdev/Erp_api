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
    if (typeof opcoes.body === 'string' && !headers.has('Content-Type')) {
        try {
            JSON.parse(opcoes.body)
            headers.set('Content-Type', 'application/json')
        } catch {
            // Corpos que não são JSON mantêm seu tipo original.
        }
    }

    let resposta: Response
    try {
        resposta = await fetch(caminho, { ...opcoes, headers })
    } catch {
        throw new Error('Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.')
    }

    const mensagens: Record<number, string> = {
        400: 'Dados inválidos. Confira os campos e tente novamente.',
        401: 'Sua sessão expirou ou não é válida. Entre novamente no sistema.',
        403: 'Você não tem permissão para acessar este recurso.',
        404: 'O recurso solicitado não foi encontrado.',
        409: 'A operação entrou em conflito com os dados atuais. Atualize os dados e tente novamente.',
    }

    if (!resposta.ok) {
        let mensagem = mensagens[resposta.status]
            ?? `Não foi possível concluir a solicitação (HTTP ${resposta.status}). Tente novamente.`
        // ExceptionsHandler retorna { message, dateError } para erros de negócio.
        // Não exibimos corpos brutos, erros internos (5xx) ou detalhes de exceções.
        if ([400, 403, 404, 409].includes(resposta.status)) {
            try {
                const erro: unknown = await resposta.json()
                if (erro && typeof erro === 'object' && 'message' in erro) {
                    const texto = erro.message
                    if (typeof texto === 'string' && texto.trim() && texto.length <= 600
                        && !/[<>\r\n]|exception|stacktrace|\bat\s+\w+\.|\b(?:java|org|com)\.|\bsql\b|jdbc/i.test(texto)) {
                        mensagem = texto.trim()
                    }
                }
            } catch {
                // Respostas vazias ou não JSON usam a mensagem padrão do status.
            }
        }
        throw new Error(mensagem)
    }

    try {
        const corpo = await resposta.text()
        // Inclui respostas 204 e outras respostas bem-sucedidas sem conteúdo.
        return corpo.trim() ? JSON.parse(corpo) as T : undefined
    } catch {
        throw new Error('Não foi possível ler a resposta do servidor. Tente novamente.')
    }
}
