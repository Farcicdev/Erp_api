import { Navigate, Route, Routes } from 'react-router'
import { AppLayout } from '../components/AppLayout'
import { DashboardPage } from '../pages/DashboardPage'
import { LojasPage } from '../pages/LojasPage'
import { RequireAdminCliente } from './RequireAdminCliente'

export function AppRoutes() {
    return (
        <Routes>
            <Route element={<AppLayout />}>
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route element={<RequireAdminCliente />}>
                    <Route path="/lojas" element={<LojasPage />} />
                </Route>
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Route>
        </Routes>
    )
}
