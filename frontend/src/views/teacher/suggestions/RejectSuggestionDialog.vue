<template>
  <v-dialog
    :value="dialog"
    @input="closeQuestionDialog"
    @keydown.esc="closeQuestionDialog"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ 'Reject Suggestion' }}</span>
      </v-card-title>

      <v-subheader>Justification (Optional) </v-subheader>
      <v-flex xs12 sm12 md12>
        <v-textarea
          outline
          rows="10"
          v-model="suggestion.teacherExplanation"
          label="Content"
          data-cy="content"
        ></v-textarea>
      </v-flex>
      <br />

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="primary" @click="closeQuestionDialog"
          >Cancel</v-btn
        >

        <v-btn
          dark
          color="red darken-1"
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
