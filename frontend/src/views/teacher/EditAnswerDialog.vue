<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-edit-answer-dialog', false)"
    @keydown.esc="$emit('close-answer-answer-dialog', false)"
    max-width="75%"
    max-height="100px"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'Edit Answer' }}
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="answer"
                label="Answer"
                @keyup.enter.exact="
                  editAnswer() && $emit('close-edit-answer-dialog', false)
                "
                data-cy="editAnswerBox"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          @click="$emit('close-edit-answer-dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="primary" @click="editAnswer">Save Edit</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Post from '@/models/management/Post';
import { PostAnswer } from '@/models/management/PostAnswer';

@Component
export default class EditAnswerDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;

  answer: string = '';

  async editAnswer() {
    if (this.answer.trim() == '') {
      await this.$store.dispatch('error', 'Error: Answer must have content ');
      return;
    }

    try {
      let newAnswer = new PostAnswer();
      newAnswer.post = new Post();
      newAnswer.post.id = this.post.id;
      newAnswer.teacherAnswer = this.answer;
      await RemoteServices.updateAnswer(newAnswer);
      this.$emit('save-post-edit-answer', newAnswer);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
