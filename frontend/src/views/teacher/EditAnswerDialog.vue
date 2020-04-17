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
                v-model="newAnswer.answer.teacherAnswer"
                label="Answer"
                @keyup.enter.exact="
                  editAnswer() && $emit('close-edit-answer-dialog', false)
                "
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-edit-answer-dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="editAnswer">Save Edit</v-btn>
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

  newAnswer!: Post;

  created() {
    this.newAnswer = new Post(this.post);
    console.log(this.newAnswer);
  }

  // TODO use EasyMDE with these configs
  // markdownConfigs: object = {
  //   status: false,
  //   spellChecker: false,
  //   insertTexts: {
  //     image: ['![image][image]', '']
  //   }
  // };

  async editAnswer() {
    if (this.newAnswer.answer) {
      try {
        this.newAnswer.answer.post = new Post();
        this.newAnswer.answer.post.id = this.post.id;
        await RemoteServices.updateAnswer(this.newAnswer.answer);
        this.$emit('save-post-answer', this.newAnswer);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
