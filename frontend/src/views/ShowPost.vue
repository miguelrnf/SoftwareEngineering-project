<template>
  <div class="text-left">
    <div class="mt-3 ml-3">
      <div class="headline grey--text font-weight-bold">
        {{ post.question.question.content }}
      </div>
      <div class="ml-5 mt-3">
        {{ post.question.studentQuestion }}
      </div>
      <div class="font-weight-light text-right mr-3">
        {{ post.question.user.username }}
      </div>
    </div>
    <v-divider inset class="mt-3" />
    <div
      v-if="
        post.answer != null &&
          post.answer.teacherAnswer !== '' &&
          (!post.answerPrivacy || isTeacher() || isOwner(post))
      "
      class="mt-3 ml-3"
    >
      <div class="headline grey--text font-weight-bold">{{ 'Answer:' }}</div>
      <div class="ml-5 mt-3">{{ post.answer.teacherAnswer }}</div>
      <div class="font-weight-light text-right mr-3">
        {{ post.answer.user.username }}
      </div>
    </div>
    <edit-post-dialog
      v-model="editPostDialog"
      :post="post"
      v-on:save-post-edit="onSavePostEvent"
      v-on:close-edit-post-dialog="onCloseEditPostDialog"
    />
    <edit-answer-dialog
      v-if="post.answer"
      v-model="editAnswerDialog"
      :post="post"
      v-on:save-post-edit-answer="onSavePostEvent"
      v-on:close-edit-answer-dialog="onCloseEditAnswerDialog"
    />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';
import PostStatusButtons from '@/views/PostStatusButtons.vue';
import EditPostDialog from '@/views/EditPostDialog.vue';
import EditAnswerDialog from '@/views/teacher/EditAnswerDialog.vue';

@Component({
  components: {
    'edit-post-dialog': EditPostDialog,
    'edit-answer-dialog': EditAnswerDialog,
    'post-status-buttons': PostStatusButtons
  }
})
export default class ShowPost extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;
  editPostDialog: boolean = false;
  editAnswerDialog: boolean = false;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  isOwner(post: Post): boolean {
    if (this.$store.getters.getUser != null) {
      return (
        this.$store.getters.getUser.username === post.question.user.username
      );
    } else return false;
  }

  editPost() {
    this.editPostDialog = true;
  }

  editAnswer() {
    this.editAnswerDialog = true;
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }

  onCloseEditPostDialog() {
    this.editPostDialog = false;
  }

  onCloseEditAnswerDialog() {
    this.editAnswerDialog = false;
  }

  onSavePostEvent() {
    this.$emit('save-post', this.post);
  }
}
</script>

<style />
