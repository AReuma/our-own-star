// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Vuetify
import { createVuetify } from 'vuetify'

const OurOwnStar = {
    dark: false,
    colors: {
        pink: '#FF1493',
        blue: '#3498DB'
    }
}

export default createVuetify({
    theme: {
        defaultTheme: 'OurOwnStar',
        themes: {
            OurOwnStar
        }
    }
});