<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card v-if="quiz">
      <v-card-title>{{ quiz.title }}</v-card-title>

      <v-card-text>
        <show-question-list :questions="quiz.questions" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="primary" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import { Quiz } from '@/models/management/Quiz';
import ShowQuestionList from '@/views/teacher/questions/ShowQuestionList.vue';

@Component({
  components: {
    'show-question-list': ShowQuestionList
  }
})
export default class ShowQuizDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Quiz, required: true }) readonly quiz!: Quiz | null;
}
</script>
