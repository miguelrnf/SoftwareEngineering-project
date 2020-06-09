<template>
  <v-dialog
    width="50%"
    :value="dialog"
    @input="$emit('close-show-theme-dialog', false)"
    @keydown.esc="$emit('close-show-theme-dialog', false)"
  >
    <v-img contain :src="url" />
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

@Component
export default class themePreviewDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: String, required: false }) readonly url!: '';
}
</script>

<style lang="scss" scoped />
