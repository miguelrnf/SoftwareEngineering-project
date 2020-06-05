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
        success: '#4CAF50'
      },
      dark: {}
    }
  }
});

export default vuetify;
