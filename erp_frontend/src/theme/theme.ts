import { createTheme } from '@mui/material/styles'

export const theme = createTheme({
    palette: {
        primary: { main: '#2563eb' },
        background: { default: '#f4f6f8', paper: '#ffffff' },
        text: { primary: '#1f2937' },
    },
    typography: { fontFamily: 'Arial, sans-serif' },
    shape: { borderRadius: 8 },
    components: {
        MuiButton: { styleOverrides: { root: { textTransform: 'none' } } },
    },
})
