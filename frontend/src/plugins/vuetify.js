// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Vuetify
import { createVuetify } from 'vuetify'

const OurOwnStar = {
    dark: false,
    colors: {
        pink: '#FF1493',
        blue: '#3498DB',
        backColor: '#F0F0F0',
        skyblue: '#92C4F4',
        gray: '#D9D9D9',
        dateColor: '#868686',
        black: '#000000',
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
