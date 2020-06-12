<template>
  <v-dialog
    width="50%"
    :value="dialog"
    @input="$emit('close-show-theme-dialog', false)"
    @keydown.esc="$emit('close-show-theme-dialog', false)"
  >
    <v-card :dark="dark">
      <v-card class="ma-2" :color="theme.primary">
        Primary
      </v-card>
      <v-row>
        <v-col>
          <v-card class="ma-2" :color="theme.secondary">
            Secondary
          </v-card>
          <v-card class="ma-2" :color="theme.accent">
            Accent
          </v-card>
          <v-card class="ma-2" :color="theme.warning">
            Warning
          </v-card>
        </v-col>
        <v-col>
          <v-card class="ma-2" :color="theme.info">
            Info
          </v-card>
          <v-card class="ma-2" :color="theme.success">
            Success
          </v-card>
          <v-card class="ma-2" :color="theme.error">
            Error
          </v-card>
        </v-col>
      </v-row>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { Theme } from '@/models/management/Theme';

@Component
export default class themePreviewDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Theme, required: true }) readonly theme!: Theme;
}
</script>

<style lang="scss" scoped />
