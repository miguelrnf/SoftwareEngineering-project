<template>
  <v-card max-height="350" min-height="100" outlined hover>
    <v-row>
      <v-card-title class="mt-n2 ml-3">{{
        'Suggestion ' + suggestion.title
      }}</v-card-title>
      <v-spacer />
      <div class="mr-6 mt-3">
        <v-chip
          class="ma-1"
          x-small
          label
          :color="getColor1(suggestion.isprivate)"
          dark
        />
        <v-chip
          class="ma-1"
          x-small
          label
          :color="getColor2(suggestion.status)"
          dark
        />
      </div>
      <v-card-text>
        <div class="mt-n4 text-left">
          <v-card-subtitle>Suggestion:</v-card-subtitle>

          <v-card-text class="mt-n3">{{ suggestion._questionStr }}</v-card-text>
        </div>
      </v-card-text>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Suggestion from '@/models/management/Suggestion';

@Component
export default class SuggsPreview extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getColor1(IsPrivate: boolean) {
    let vazo = 'black';
    if (IsPrivate) return vazo;
    else return 'orange';
  }

  getColor2(Status: string) {
    if (Status == 'TOAPPROVE') return 'yellow';
    else if (Status == 'REJECTED') return 'red';
    else return 'green';
  }
}
</script>
<style scoped />
