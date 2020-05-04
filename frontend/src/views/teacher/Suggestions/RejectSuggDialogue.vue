<template>
  <v-dialog v-model="dialog" @keydown.esc="closeQuestionDialog" max-width="75%">
    <v-card>
      <v-card-title>
        <span class="headline">{{ 'Reject Suggestion' }}</span>
      </v-card-title>

      <v-subheader>Justification (Optional) </v-subheader>
      <v-flex xs12 sm12 md12>
        <v-textarea
          outline
          rows="10"
          v-model="suggestion._justification"
          label="Content"
          data-cy="content"
        ></v-textarea>
      </v-flex>
      <br />

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeQuestionDialog"
          >Cancel</v-btn
        >

        <v-btn dark color="red darken-1" @click="RejectSuggestion"
          >Reject</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class RejectSuggestionDialog extends Vue {
  @Prop({ type: Suggestion, required: true }) suggestion!: Suggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  async RejectSuggestion() {
    this.suggestion.status = 'REJECTED';

    if (this.suggestion._justification == '') {
      this.suggestion._justification = 'No justification was given';
    }
    const result = await RemoteServices.approveSuggestion(this.suggestion);
    console.log (this.suggestion);

    this.$emit('save-suggestion', result);
  }
  closeQuestionDialog() {
    this.$emit('close-rejection');
  }
}
</script>
