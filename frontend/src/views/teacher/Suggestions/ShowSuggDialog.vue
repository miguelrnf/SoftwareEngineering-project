<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card class="mx-auto" max-height="80%">
      <v-app-bar dense color="grey lighten-2">
        <v-row>
          <v-card-title class="mt-n2 ml-3">{{
            'Suggestion' + suggestion._id
          }}</v-card-title>

          <v-spacer />
          <div class="mr-6 mt-3">
            <v-chip
              class="ma-1"
              x-small
              label
              :color="getColor1(suggestion._isprivate)"
              text-color="white"
              dark
              ><span class="white--text ">{{
                getPrivacyTag(suggestion._isprivate)
              }}</span></v-chip
            >
            <v-chip
              class="ma-1"
              x-small
              label
              :color="getColor2(suggestion.status)"
              dark
              ><span class="white--text ">{{ suggestion.status }}</span></v-chip
            >
          </div>
        </v-row>
      </v-app-bar>

      <v-card-text>
        <p class="headline font-weight-black text-left">
          <span v-html="convertMarkDown(suggestion.title)" />
        </p>
        <div class="headline text-left">
          <span v-html="convertMarkDown(suggestion._questionStr)" />
        </div>
        <v-row>
          <span v-html="convertMarkDown('Options: ')" />
          <v-chip
            v-for="option in suggestion.options"
            :key="option.id"
            class="ma-1"
            x-small
            :color="getChipColor(option.correct)"
            outlined
            :text-color="getTextColor(option.correct)"
            dark
            >{{ option.content }}
          </v-chip>
        </v-row>
        <v-row>
          <span v-html="convertMarkDown('Topics: ')" />
          <v-chip
            v-for="option in suggestion._topicsList"
            :key="option.id"
            class="ma-1"
            x-small
            color="grey"
            text-color="white"
            dark
            ><span class="white--text">{{ option.name }}</span>
          </v-chip>
        </v-row>
        <div class="text-right">
          by
          <span
            v-html="
              convertMarkDown(
                suggestion._student.username + ' on ' + suggestion.creationDate
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
          <span>{{ suggestion._justification }}</span>
        </v-card-text>
      </h1>
      <br />

      <v-card-actions>
        <v-spacer />
        <v-btn
          dark
          color="blue darken-1"
          @click="closeQuestionDialog"
          data-cy="closeSuggestionButton"
          >close</v-btn
        >

        <h1 v-if="suggestion.status == 'TOAPPROVE'">
          <v-btn
            dark
            color="green darken-1"
            @click="ApproveSuggestion"
            data-cy="approveSuggestionButton"
            >Approve</v-btn
          >

          <v-btn
            dark
            color="red darken-1"
            @click="RejectSuggestion"
            data-cy="rejectSuggestionButton"
            >Reject</v-btn
          >
        </h1>
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
export default class ShowSuggDialog extends Vue {
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
    if (Status == 'TOAPPROVE') return 'yellow';
    else if (Status == 'REJECTED') return 'red';
    else return 'green';
  }
}
</script>
