<template>
  <v-card class="mx-auto" max-height="80%">
    <v-app-bar dense color="grey lighten-2">
      <v-toolbar-title>
        {{ convertMarkDown(post.question.question.title) }}</v-toolbar-title
      >
      <v-spacer />
      <post-status-buttons :post="post"></post-status-buttons>
      <v-tooltip bottom v-if="isOwner(post)">
        <template v-slot:activator="{ on }">
          <v-icon small class="mr-2" v-on="on" @click="editPost(post)"
            >edit</v-icon
          >
        </template>
        <span>Edit Post</span>
      </v-tooltip>
      <v-tooltip bottom v-if="isTeacher() && post.answer != null">
        <template v-slot:activator="{ on }">
          <v-icon small class="mr-2" v-on="on" @click="editAnswer(post)"
            >edit</v-icon
          >
        </template>
        <span>Edit Answer</span>
      </v-tooltip>
      <v-menu left bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item v-for="n in 5" :key="n" @click="() => {}">
            <v-list-item-title>Option {{ n }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
    <v-card-text>
      <p class="headline font-weight-black">
        <span v-html="convertMarkDown(post.question.question.content)" />
      </p>
      <div class="headline text-left">
        <span v-html="convertMarkDown(post.question.studentQuestion)" />
      </div>
      <div class="text-right">
        by
        <span v-html="convertMarkDown(post.question.user.username)" />
      </div>
    </v-card-text>
    <v-card-text v-if="post.answer != null && post.answer.teacherAnswer !== ''">
      <p class="subtitle-1 font-weight-light">
        <span v-html="convertMarkDown('Answer:')" />
      </p>
      <p class="headline font-weight-dark">
        <span v-html="convertMarkDown(post.answer.teacherAnswer)" />
      </p>
      <div class="text-right">
        by
        <span v-html="convertMarkDown(post.answer.user.username)" />
      </div>
    </v-card-text>
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';
import PostStatusButtons from '@/views/PostStatusButtons.vue';
import EditPostDialog from '@/views/EditPostDialog.vue';
import EditAnswerDialog from '@/views/teacher/EditAnswerDialog.vue';
import { PostAnswer } from '@/models/management/PostAnswer';

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
    return this.$store.getters.getUser.username === post.question.user.username;
  }

  editPost(post: Post) {
    this.editPostDialog = true;
  }

  editAnswer(post: Post) {
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

<style>
</style>
