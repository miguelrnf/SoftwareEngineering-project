import Vue from 'vue';
import Vuetify from 'vuetify/lib';
import 'vuetify/dist/vuetify.min.css';
import '@fortawesome/fontawesome-free/css/all.css';
import '@mdi/font/css/materialdesignicons.css';
import 'material-design-icons-iconfont/dist/material-design-icons.css';

Vue.use(Vuetify);

const vuetify = new Vuetify({
  icons: {
    iconfont: 'mdi'
  },
  iconfont: 'fa',
  theme: {
    themes: {
      light: {
        primary: '#1976D2',
        accent: '#828282',
        secondary: '#d8d8d8',
        info: '#2196F3',
        warning: '#FB8C00',
        error: '#FF5252',
        success: '#4CAF50',
        background: '#ffffff',
        font: '#000000'
      },
      dark: {
        primary: '#25302b',
        accent: '#829ab1',
        secondary: '#393e46',
        info: '#4ecca3',
        warning: '#102a43',
        error: '#ec625f',
        success: '#cee397',
        background: '#1E1E1E',
        font: '#ffffff'
      }
    }
  }
});

export default vuetify;
