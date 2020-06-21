<template>
  <v-dialog
    :value="dialog"
    @input="closeQuestionDialog"
    @keydown.esc="closeQuestionDialog"
    max-width="75%"
  >
    <v-card class="mx-auto" max-height="80%">
      <v-app-bar dense color="primary">
        <v-toolbar-title class="white--text">{{
          'Suggestion ' + suggestion.id
        }}</v-toolbar-title>
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
      <show-suggestion :suggestion="this.suggestion"></show-suggestion>
      <div v-if="suggestion.status === 'REJECTED'">
        <v-app-bar dense color="primary">
          <v-toolbar-title class="white--text">{{
            'Justification'
          }}</v-toolbar-title>
        </v-app-bar>
        <v-card-text class="text-left">
          <span>{{ suggestion.teacherExplanation }}</span>
        </v-card-text>
      </div>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="closeQuestionDialog">
          Close
        </v-btn>
        <div v-if="suggestion.status === 'TOAPPROVE' && isTeacher()">
          <v-btn color="red" text @click="RejectSuggestion">
            Reject
          </v-btn>
          <v-btn color="green" text @click="ApproveSuggestion">
            Approve
          </v-btn>
        </div>
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
import ShowSuggestion from '@/views/ShowSuggestion.vue';

@Component({
  components: {
    'show-suggestion': ShowSuggestion
  }
})
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

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
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
