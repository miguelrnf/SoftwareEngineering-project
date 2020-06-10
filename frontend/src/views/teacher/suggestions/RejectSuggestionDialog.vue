<template>
  <v-dialog
    :value="dialog"
    @input="closeQuestionDialog"
    @keydown.esc="closeQuestionDialog"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-app-bar dense color="primary">
        <v-card-title class="white--text">{{
          'Reject This Suggestion'
        }}</v-card-title>
      </v-app-bar>

      <v-flex xs12 sm12 md12>
        <v-textarea
          outline
          rows="10"
          v-model="suggestion.teacherExplanation"
          label="Write a brief explanation for the student to understand why his suggestion was rejected"
          data-cy="content"
        ></v-textarea>
      </v-flex>
      <br />

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" text @click="closeQuestionDialog"
          >Cancel</v-btn
        >
        <v-btn
          color="red darken-1"
          text
          @click="RejectSuggestion"
          data-cy="rejectButton"
          >Reject</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class RejectSuggestionDialog extends Vue {
  @Prop({ type: Suggestion, required: true }) suggestion!: Suggestion;
  @Model('dialog', Boolean) dialog!: boolean;

  async RejectSuggestion() {
    this.suggestion.status = 'REJECTED';

    if (this.suggestion.teacherExplanation == '') {
      this.suggestion.teacherExplanation = 'No justification was given';
    }
    const result = await RemoteServices.approveSuggestion(this.suggestion);
    this.$emit('save-suggestion', result);
  }
  closeQuestionDialog() {
    this.$emit('close-rejection');
  }
}
</script>
