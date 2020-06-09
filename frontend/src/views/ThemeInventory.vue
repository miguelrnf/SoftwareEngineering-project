<template>
  <div>
    <v-card class="mx-auto mt-4" width="95%">
      <v-card color="primary" class="white--text">
        <v-card-title style="font-size: xx-large">
          <v-icon class="pr-2" color="white">
            fas fa-cogs
          </v-icon>
          Owned Themes
        </v-card-title>
      </v-card>
      <v-text-field
        counter
        maxlength="50"
        prepend-icon="search"
        clearable
        class="mx-4"
        label="Search"
        v-model="search"
        @input="filterThemes"
      >
      </v-text-field>
      <v-row no-gutters>
        <v-col cols="4" v-for="n in filteredThemes" :key="n.title">
          <v-card outlined tile class="ma-3">
            <v-img
              class="align-end"
              style="cursor: pointer"
              contain
              :src="n.image"
              @click="openDialog(n.image)"
            />
            <v-card-actions>
              <v-card-title class="pa-0" style="font-size: x-large">{{
                n.title
              }}</v-card-title>
              <v-spacer />
              <v-btn @click="applyTheme(n)" color="primary">
                Apply
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-card>
    <theme-preview
      :url="currentTheme"
      :dialog="themeDialog"
      v-on:close-show-theme-dialog="closeDialog"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ThemePreviewDialog from '@/views/ThemePreviewDialog';

@Component({
  components: {
    'theme-preview': ThemePreviewDialog
  }
})
export default class ThemeInventory extends Vue {
  themeDialog: boolean = false;
  currentTheme: String = '';
  search: string = '';
  filteredThemes: any[] = [];
  testThemes = [
    {
      title: 'Default Light',
      dark: false,
      colors: {
        primary: '#1976D2',
        accent: '#828282',
        secondary: '#d8d8d8',
        info: '#2196F3',
        warning: '#FB8C00',
        error: '#FF5252',
        success: '#4CAF50'
      },
      image: 'https://imgur.com/H2VUg7T.png'
    },
    {
      title: 'Default Dark',
      dark: true,
      colors: {
        primary: '#25302b',
        accent: '#829ab1',
        secondary: '#393e46',
        info: '#4ecca3',
        warning: '#102a43',
        error: '#ec625f',
        success: '#cee397'
      },
      image: 'https://imgur.com/ypuL0jJ.png'
    },
    {
      title: 'Test Light',
      dark: false,
      colors: {
        primary: '#1976D2',
        accent: '#828282',
        secondary: '#d8d8d8',
        info: '#2196F3',
        warning: '#FB8C00',
        error: '#FF5252',
        success: '#4CAF50'
      },
      image: 'https://imgur.com/H2VUg7T.png'
    },
    {
      title: 'Test Dark',
      dark: true,
      colors: {
        primary: '#25302b',
        accent: '#829ab1',
        secondary: '#393e46',
        info: '#4ecca3',
        warning: '#102a43',
        error: '#ec625f',
        success: '#cee397'
      },
      image: 'https://imgur.com/ypuL0jJ.png'
    }
  ];

  async created() {
    this.filteredThemes = this.testThemes;
  }

  filterThemes() {
    console.log(this.search);
    this.filteredThemes = this.testThemes.filter(t =>
      t.title.includes(this.search)
    );
  }

  openDialog(image: string) {
    this.themeDialog = true;
    this.currentTheme = image;
  }

  closeDialog() {
    this.themeDialog = false;
    this.currentTheme = '';
  }

  applyTheme(theme: any) {
    this.$vuetify.theme.dark = theme.dark;
    this.$vuetify.theme.currentTheme.primary = theme.color.primary;
    this.$vuetify.theme.currentTheme.accent = theme.color.accent;
    this.$vuetify.theme.currentTheme.secondary = theme.color.secondary;
    this.$vuetify.theme.currentTheme.info = theme.color.info;
    this.$vuetify.theme.currentTheme.warning = theme.color.warning;
    this.$vuetify.theme.currentTheme.error = theme.color.error;
    this.$vuetify.theme.currentTheme.success = theme.color.success;
  }
}
</script>

<style scoped></style>
