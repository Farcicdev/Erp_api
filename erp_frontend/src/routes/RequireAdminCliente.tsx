import { Navigate, Outlet } from 'react-router'
import keycloak from '../auth/keycloak'

export function RequireAdminCliente() {
    // Proteção de navegação; a autorização dos dados continua no backend.
    return keycloak.hasResourceRole('ADMIN_CLIENTE', 'erp-api')
        ? <Outlet />
        : <Navigate to="/dashboard" replace />
}
