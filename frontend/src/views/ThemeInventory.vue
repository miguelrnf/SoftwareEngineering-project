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
        <v-col cols="4" v-for="n in filteredThemes" :key="n.userItemDto.name">
          <v-card
            outlined
            :color="isCurrent(n) ? 'primary' : ''"
            tile
            class="ma-3"
          >
            <v-card :light="!n.dark" :dark="n.dark" tile class="ma-1 pa-1">
              <v-chip :light="!n.dark" :color="n.primary" small :dark="n.dark"
                >Primary</v-chip
              >
              <v-chip
                :light="!n.dark"
                :color="n.secondary"
                small
                :dark="n.dark"
              >
                Secondary
              </v-chip>
              <v-chip :light="!n.dark" :color="n.accent" small :dark="n.dark"
                >Accent
              </v-chip>
              <v-chip :light="!n.dark" :color="n.info" small :dark="n.dark">
                Info
              </v-chip>
              <v-chip :light="!n.dark" :color="n.warning" small :dark="n.dark">
                Warning
              </v-chip>
              <v-chip :light="!n.dark" :color="n.error" small :dark="n.dark">
                Error
              </v-chip>
              <v-chip :light="!n.dark" :color="n.success" small :dark="n.dark">
                Success
              </v-chip>
              <v-icon style="cursor: pointer" @click="openDialog(n)">
                search
              </v-icon>
            </v-card>

            <v-card-actions>
              <v-card-title class="pa-0" style="font-size: x-large">{{
                n.userItemDto.name
              }}</v-card-title>
              <v-spacer />
              <v-btn
                @click="applyTheme(n)"
                :color="isCurrent(n) ? 'accent' : 'primary'"
              >
                {{ isCurrent(n) ? 'Applied' : 'Apply' }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-card>
    <theme-preview
      :theme="currentTheme"
      :dialog="themeDialog"
      v-on:close-show-theme-dialog="themeDialog = false"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Theme } from '@/models/management/Theme';
import ThemePreviewDialog from './ThemePreviewDialog.vue';

@Component({
  components: {
    'theme-preview': ThemePreviewDialog
  }
})
export default class ThemeInventory extends Vue {
  themeDialog: boolean = false;
  currentTheme: Theme = new Theme();
  search: string = '';
  filteredThemes: Theme[] = [];
  themes: Theme[] = [];

  async created() {
    this.themes = await RemoteServices.getThemes();
    this.filteredThemes = this.themes;
    this.currentTheme = this.themes[0];
  }

  filterThemes() {
    this.filteredThemes = this.themes.filter(t =>
      t.userItemDto.name.includes(this.search)
    );
  }

  openDialog(theme: Theme) {
    this.themeDialog = true;
    this.currentTheme = theme;
  }

  async applyTheme(theme: Theme) {
    await RemoteServices.updateCurrentTheme(theme.userItemDto.name);
    await this.$store.dispatch('updateUser');
    this.$vuetify.theme.dark = theme.dark;
    this.$vuetify.theme.currentTheme.primary = theme.primary;
    this.$vuetify.theme.currentTheme.accent = theme.accent;
    this.$vuetify.theme.currentTheme.secondary = theme.secondary;
    this.$vuetify.theme.currentTheme.info = theme.info;
    this.$vuetify.theme.currentTheme.warning = theme.warning;
    this.$vuetify.theme.currentTheme.error = theme.error;
    this.$vuetify.theme.currentTheme.success = theme.success;
  }

  isCurrent(theme: Theme): boolean {
    return theme.userItemDto.name === this.$store.getters.getUser.currentTheme;
  }
}
</script>

<style scoped />
