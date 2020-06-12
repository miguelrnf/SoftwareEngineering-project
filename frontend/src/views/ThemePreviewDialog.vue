<template>
  <v-dialog
    width="50%"
    :value="dialog"
    @input="$emit('close-show-theme-dialog', false)"
    @keydown.esc="$emit('close-show-theme-dialog', false)"
  >
    <v-card :dark="theme.dark" :light="!theme.dark">
      <v-row :justify="'center'" class="ma-0">
        <v-col>
          <v-card class="my-4 py-2" :color="theme.primary">
            Primary
          </v-card>
        </v-col>
      </v-row>
      <v-row :justify="'center'" class="ma-0">
        <v-col cols="5">
          <v-card class="my-2 py-2" :color="theme.secondary">
            Secondary
          </v-card>
          <v-card class="my-2 py-2" :color="theme.accent">
            Accent
          </v-card>
          <v-card class="my-2 py-2" :color="theme.warning">
            Warning
          </v-card>
        </v-col>
        <v-col cols="5">
          <v-card class="my-2 py-2" :color="theme.info">
            Info
          </v-card>
          <v-card class="my-2 py-2" :color="theme.success">
            Success
          </v-card>
          <v-card class="my-2 py-2" :color="theme.error">
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
export default class ThemePreviewDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Theme, required: true }) readonly theme!: Theme;
}
</script>

<style lang="scss" scoped />
