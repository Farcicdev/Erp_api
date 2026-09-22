import { Navigate, Route, Routes } from 'react-router'
import { AppLayout } from '../components/AppLayout'
import { DashboardPage } from '../pages/DashboardPage'
import { LojasPage } from '../pages/LojasPage'
import { RequireAdminCliente } from './RequireAdminCliente'
import { RequireLojaAtual } from './RequireLojaAtual'
import { ProdutosPage } from '../pages/ProdutosPage'
import { ProdutoNovoPage } from '../pages/ProdutoNovoPage'

export function AppRoutes() {
    return (
        <Routes>
            <Route element={<AppLayout />}>
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route element={<RequireAdminCliente />}>
                    <Route path="/lojas" element={<LojasPage />} />
                    <Route element={<RequireLojaAtual />}>
                        <Route path="/produtos" element={<ProdutosPage />} />
                        <Route path="/produtos/novo" element={<ProdutoNovoPage />} />
                    </Route>
                </Route>
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Route>
        </Routes>
    )
}
