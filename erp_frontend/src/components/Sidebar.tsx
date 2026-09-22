import { Box, Drawer, List, ListItemButton, ListItemText } from '@mui/material'
import { NavLink } from 'react-router'
import keycloak from '../auth/keycloak'

type SidebarProps = {
    aberto: boolean
    onFechar: () => void
    largura: number
}

export function Sidebar({ aberto, onFechar, largura }: SidebarProps) {
    // A visibilidade do menu não substitui a autorização feita pelo backend.
    const ehAdminCliente = keycloak.hasResourceRole('ADMIN_CLIENTE', 'erp-api')
    const menu = (
        <>
            <List>
                <ListItemButton component={NavLink} to="/dashboard" onClick={onFechar}
                    sx={{ '&.active': { bgcolor: 'action.selected' } }}>
                    <ListItemText primary="Dashboard" />
                </ListItemButton>
                {ehAdminCliente && (
                    <>
                        <ListItemButton component={NavLink} to="/lojas" onClick={onFechar}
                            sx={{ '&.active': { bgcolor: 'action.selected' } }}>
                            <ListItemText primary="Minhas lojas" />
                        </ListItemButton>
                        <ListItemButton component={NavLink} to="/produtos" onClick={onFechar}
                            sx={{ '&.active': { bgcolor: 'action.selected' } }}>
                            <ListItemText primary="Produtos" />
                        </ListItemButton>
                    </>
                )}
            </List>
        </>
    )

    return (
        <Box component="nav" aria-label="Navegação principal" sx={{ width: { md: largura }, flexShrink: 0 }}>
            <Drawer variant="temporary" open={aberto} onClose={onFechar}
                sx={{ display: { xs: 'block', md: 'none' }, '& .MuiDrawer-paper': { width: largura } }}>
                {menu}
            </Drawer>
            <Drawer variant="permanent"
                sx={{ display: { xs: 'none', md: 'block' }, height: '100%', '& .MuiDrawer-paper': { width: largura, position: 'relative', height: '100%' } }}>
                {menu}
            </Drawer>
        </Box>
    )
}
