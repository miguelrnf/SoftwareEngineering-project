<template>
  <v-dialog
    :value="dialog"
    @input="closeQuestionDialog"
    @keydown.esc="closeQuestionDialog"
    max-width="75%"
  >
    <v-card class="mx-auto" max-height="80%">
      <v-app-bar dense color="grey lighten-2">
        <v-toolbar-title>{{ 'Suggestion' + suggestion.id }}</v-toolbar-title>
        <v-spacer />
        <div>
          <v-chip
            class="ma-1"
            small
            label
            :color="getColor1(suggestion.isPrivate)"
            text-color="white"
            dark
            >{{ getPrivacyTag(suggestion.isPrivate) }}</v-chip
          >
          <v-chip
            class="ma-1"
            small
            label
            :color="getColor2(suggestion.status)"
            text-color="white"
            dark
            >{{ suggestion.status }}</v-chip
          >
        </div>
      </v-app-bar>
      <v-card-text>
        <p class="headline font-weight-black text-left mt-3">
          <span v-html="convertMarkDown(suggestion.title)" />
        </p>
        <div class="headline text-left">
          <span v-html="convertMarkDown(suggestion.studentQuestion)" />
        </div>
        <v-divider></v-divider>
        <div class="mt-2 text-left">
          <span class="headline font-weight-black mr-10">
            {{ 'Options' }}
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
        <v-divider class="mt-4"></v-divider>
        <div class="mt-2 text-left">
          <span class="headline font-weight-black mr-10">
            {{ 'Topics' }}
          </span>
          <div class="mt-3">
            <v-chip
              v-for="option in suggestion.topicsList"
              :key="option.id"
              class="ma-1"
              color="grey"
              text-color="white"
              dark
              ><span class="white--text">{{ option.name }}</span>
            </v-chip>
          </div>
        </div>
        <div class="text-right mb-n10">
          <span
            v-html="
              convertMarkDown(
                suggestion.student.username + ' on ' + suggestion.creationDate
              )
            "
          />
        </div>
      </v-card-text>
      <h1 v-if="suggestion.status === 'REJECTED'">
        <v-app-bar dense color="grey lighten-2">
          <v-card-title class="mt-n2 ml-3">{{ 'Justification' }}</v-card-title>
        </v-app-bar>

        <v-card-text class="text-left">
          <span>{{ suggestion.teacherExplanation }}</span>
        </v-card-text>
      </h1>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="closeQuestionDialog">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import RemoteServices from '@/services/RemoteServices';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';

@Component
export default class SuggViewDialog extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  async ApproveSuggestion() {
    this.suggestion.status = 'APPROVED';
    const result = await RemoteServices.approveSuggestion(this.suggestion);
    this.$emit('close-show-suggestion-dialog', result);
  }

  async RejectSuggestion() {
    this.$emit('reject-suggestion');
  }

  closeQuestionDialog() {
    this.$emit('close-show-suggestion-dialog');
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

  getPrivacyTag(isprivate: boolean) {
    if (isprivate) return 'PRIVATE';
    else return 'PUBLIC';
  }

  getColor1(IsPrivate: boolean) {
    let vazo = 'black';
    if (IsPrivate) return vazo;
    else return 'orange';
  }

  getColor2(Status: string) {
    if (Status == 'TOAPPROVE') return 'yellow darken-2';
    else if (Status == 'REJECTED') return 'red';
    else return 'green';
  }
}
</script>
