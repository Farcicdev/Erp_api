import { useState } from 'react'
import { Box, Toolbar } from '@mui/material'
import { Outlet } from 'react-router'
import { Sidebar } from './Sidebar'
import { Topbar } from './Topbar'

export function AppLayout() {
    const [menuAberto, setMenuAberto] = useState(false)

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh' }}>
            <Topbar menuAberto={menuAberto} onAbrirMenu={() => setMenuAberto(true)} />
            <Sidebar aberto={menuAberto} onFechar={() => setMenuAberto(false)} largura={240} />
            <Box component="main" sx={{ flexGrow: 1, minWidth: 0, p: { xs: 2, sm: 3 } }}>
                <Toolbar />
                <Outlet />
            </Box>
        </Box>
    )
}
