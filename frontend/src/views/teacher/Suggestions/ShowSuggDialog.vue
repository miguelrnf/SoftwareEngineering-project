<template>
  <v-dialog
          :value="dialog"
          @input="$emit('dialog', false)"
          @keydown.esc="$emit('dialog', false)"
          max-width="75%"
          max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ 'Suggestion' }}</span>
      </v-card-title>
      <v-subheader>Creation Date:</v-subheader>
      <v-card-text class="text-left">
        <span>{{suggestion.creationDate}}</span>
      </v-card-text>

      <v-subheader>Student Username:</v-subheader>
      <v-card-text class="text-left">
        <span>{{suggestion._student.username}}</span>
      </v-card-text>

      <v-subheader>Question Content:</v-subheader>
      <v-card-text class="text-left">
        <span>{{suggestion._questionStr}}</span>
      </v-card-text>

      <v-subheader>Question Topics:</v-subheader>
      <ul>
        <li v-for="option in suggestion._topicsList" :key="option.id">
          <span class="text-left">{{option.name}}</span>
        </li>
      </ul>
      <br />

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeQuestionDialog"
               data-cy="closeSuggestionButton"

        >close</v-btn>

        <h1 v-if="suggestion.status=='TOAPPROVE'">
          <v-btn dark color="green darken-1" @click="ApproveSuggestion()"
                 data-cy="approveSuggestionButton"

          >Approve</v-btn>

          <v-btn dark color="red darken-1" @click="RejectSuggestion()"
                 data-cy="rejectSuggestionButton"
          >Reject</v-btn>

        </h1>

      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  import Suggestion from '@/models/management/Suggestion';
  import RemoteServices from '@/services/RemoteServices';



  @Component
  export default class ShowSuggDialog extends Vue {
    @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
    @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;



    async ApproveSuggestion() {
      this.suggestion.status = 'APPROVED'
      const result = await RemoteServices.approveSuggestion(this.suggestion);
      this.$emit('approve-question', result);
    }



    async RejectSuggestion() {

      this.suggestion.status = 'REJECTED'

      if (
              this.suggestion._justification == '') {
        this.suggestion._justification = 'No justification was given';
      }

      const result = await RemoteServices.approveSuggestion(this.suggestion);
      this.$emit('reject-question', result);
    }

    closeQuestionDialog() {
      this.$emit('close-show-suggestion-dialog');
    }
  }
</script>