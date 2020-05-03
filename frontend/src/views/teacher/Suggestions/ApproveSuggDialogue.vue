<template>
  <v-dialog v-model="dialog" @keydown.esc="closeQuestionDialog" max-width="75%">
    <v-card>
      <v-card-title>
        <span class="headline">{{ 'Reject Suggestion' }}</span>
      </v-card-title>
      <v-subheader><bold>Justification:</bold></v-subheader>
      <v-card-text class="text-left">
        <span>{{ suggestion.creationDate }}</span>
      </v-card-text>

      <v-subheader><bold>Student Username:</bold></v-subheader>
      <v-card-text class="text-left">
        <span>{{ suggestion._student.username }}</span>
      </v-card-text>

      <v-subheader><bold>Question Content:</bold></v-subheader>
      <v-card-text class="text-left">
        <span>{{ suggestion._questionStr }}</span>
      </v-card-text>

      <v-subheader><bold>Question Topics:</bold></v-subheader>
      <ul>
        <li v-for="option in suggestion._topicsList" :key="option.id">
          <span class="text-left">{{ option.name }}</span>
        </li>
      </ul>
      <br />

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeQuestionDialog"
          >Cancel</v-btn
        >

        <v-btn dark color="red darken-1" @click="closeQuestionDialog"
          >Reject</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Suggestion from '@/models/management/Suggestion';

@Component
export default class ShowSuggestionDialog extends Vue {
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeQuestionDialog() {
    this.$emit('close-show-suggestion-dialog');
  }
}
</script>
