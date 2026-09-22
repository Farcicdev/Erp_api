import { useState } from 'react'
import { Box } from '@mui/material'
import { Outlet } from 'react-router'
import { Sidebar } from './Sidebar'
import { Topbar } from './Topbar'

export function AppLayout() {
    const [menuAberto, setMenuAberto] = useState(false)

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100dvh' }}>
            <Topbar menuAberto={menuAberto} onAbrirMenu={() => setMenuAberto(true)} />
            <Box sx={{ display: 'flex', flex: 1, minHeight: 0 }}>
                <Sidebar aberto={menuAberto} onFechar={() => setMenuAberto(false)} largura={240} />
                <Box component="main" sx={{ flexGrow: 1, minWidth: 0, overflow: 'auto', p: { xs: 2, sm: 3 } }}>
                    <Outlet />
                </Box>
            </Box>
        </Box>
    )
}
