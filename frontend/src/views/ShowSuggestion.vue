<template>
  <v-card-text>
    <p class="headline font-weight-black text-left mt-3">
      <span v-html="convertMarkDown(suggestion.title)" />
    </p>
    <div class="headline text-left">
      <span v-html="convertMarkDown(suggestion.studentQuestion)" />
    </div>
    <v-divider />
    <div class="mt-2 text-left">
      <span class="headline font-weight-black mr-10">
        {{ 'Options:' }}
      </span>
      <div class="mt-3">
        <v-chip
          v-for="option in suggestion.options"
          :key="option.id"
          class="ma-1"
          :color="getChipColor(option.correct)"
          outlined
          :text-color="getTextColor(option.correct)"
          dark
          >{{ option.content }}
        </v-chip>
      </div>
    </div>
    <v-divider class="mt-4" />
    <div class="mt-2 text-left">
      <span class="headline font-weight-black mr-10">
        {{ 'Topics:' }}
      </span>
      <div class="mt-3">
        <v-chip
          v-for="option in suggestion.topicsList"
          :key="option.id"
          class="ma-1"
          color="grey"
          text-color="white"
          dark
        >
          <span class="white--text">{{ option.name }}</span>
        </v-chip>
      </div>
    </div>
    <div class="text-right mb-n5">
      <span
        v-html="
          convertMarkDown(
            suggestion.student.username + ' on ' + suggestion.creationDate
          )
        "
      />
    </div>
  </v-card-text>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import RemoteServices from '@/services/RemoteServices';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';

@Component
export default class ShowSuggestion extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;

  async ApproveSuggestion() {
    this.suggestion.status = 'APPROVED';
    const result = await RemoteServices.approveSuggestion(this.suggestion);
    this.$emit('close-show-suggestion-dialog', result);
  }

  async RejectSuggestion() {
    this.$emit('reject-suggestion');
  }

  getChipColor(iscorrect: boolean) {
    if (iscorrect) return 'green';
    return 'red';
  }

  getTextColor(iscorrect: boolean) {
    if (iscorrect) return 'green';
    return 'red';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style scoped />
