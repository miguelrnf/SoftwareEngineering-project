<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-edit-post-dialog', false)"
    @keydown.esc="$emit('close-edit-post-dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'Edit Post' }}
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-card-title>{{ post.question.question.title }}</v-card-title>
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editedQuestion"
                label="Question"
                @keydown.enter.exact="
                  savePostEdit() && $emit('close-edit-post-dialog', false)
                "
                data-cy="dialogEditPost"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('close-edit-post-dialog')"
          data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          @click="savePostEdit"
          data-cy="saveEditButton"
          >Save Edit</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Post from '@/models/management/Post';

@Component
export default class EditPostDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;

  editedQuestion: string = '';

  async savePostEdit() {
    if (this.editedQuestion.trim() == '') {
      await this.$store.dispatch('error', 'Error: Post must have content ');
      return;
    }

    try {
      let editedPost = new Post(this.post);
      editedPost.question.studentQuestion = this.editedQuestion;
      const result = await RemoteServices.updatePost(editedPost);
      this.$emit('save-post-edit', result);
      this.post.question.studentQuestion = this.editedQuestion;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
