<template>
  <div class="container" />
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Theme } from '@/models/management/Theme';

@Component
export default class LoginView extends Vue {
  theme: Theme = new Theme();
  async created() {
    await this.$store.dispatch('loading');
    if (this.$route.query.error) {
      await this.$store.dispatch('error', 'Fenix authentication error');
      await this.$router.push({ name: 'home' });
    } else {
      try {
        await this.$store.dispatch('fenixLogin', this.$route.query.code);
        this.theme = await RemoteServices.getCurrentTheme();
        this.applyTheme();
        await this.$router.push({ name: 'courses' });
      } catch (error) {
        await this.$store.dispatch('error', error);
        await this.$router.push({ name: 'home' });
      }
    }
    await this.$store.dispatch('clearLoading');
  }

  applyTheme() {
    this.$vuetify.theme.dark = this.theme.dark;
    this.$vuetify.theme.currentTheme.primary = this.theme.primary;
    this.$vuetify.theme.currentTheme.accent = this.theme.accent;
    this.$vuetify.theme.currentTheme.secondary = this.theme.secondary;
    this.$vuetify.theme.currentTheme.info = this.theme.info;
    this.$vuetify.theme.currentTheme.warning = this.theme.warning;
    this.$vuetify.theme.currentTheme.error = this.theme.error;
    this.$vuetify.theme.currentTheme.success = this.theme.success;
  }
}
</script>

<style lang="scss" scoped>
.btns-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .v-btn {
    margin: 5px;
  }
}
</style>
