<template>
  <div>
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.question.privacy) === 'red' &&
          isOwnerPost(post)
      "
      :color="getColor(post.discussStatus)"
      dark
      @click="changePostPrivacy(post)"
      >{{ 'Private' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.question.privacy) === 'green' &&
          isOwnerPost(post)
      "
      :color="getColor(post.discussStatus)"
      dark
      @click="changePostPrivacy(post)"
      >{{ 'Public' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.answer.privacy) === 'red' &&
          (!isOwnerAnswer(post) || post.answer == null)
      "
      :color="getColor(post.answer.privacy)"
      dark
      @click="changeAnswerPrivacy(post)"
      >{{ 'Private' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.answer.privacy) === 'green' &&
          (!isOwnerAnswer(post) || post.answer == null)
      "
      :color="getColor(post.answer.privacy)"
      dark
      @click="changeAnswerPrivacy(post)"
      >{{ 'Public' }}</v-chip
    >
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Post from '../models/management/Post';
import RemoteServices from '../services/RemoteServices';
import { PostAnswer } from '@/models/management/PostAnswer';

@Component
export default class PrivacyButton extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;

  getColor(p: boolean) {
    if (p) return 'green';
    else return 'red';
  }

  isOwnerPost(post: Post): boolean {
    return this.$store.getters.getUser.username === post.question.user.username;
  }

  isOwnerAnswer(post: Post): boolean {
    if (post.answer != null)
      return this.$store.getters.getUser.username === post.answer.user.username;
    else return false;
  }

  changeAnswerPrivacy(post: Post) {
    if (post.answer != null && this.isOwnerAnswer(post))
      post.answer.privacy = !post.answer.privacy;
    /*
    RemoteServices.changeAnswerPrivacy(post.id);
*/
  }

  changePostPrivacy(post: Post) {
    if (this.isOwnerPost(post)) post.question.privacy = !post.question.privacy;
    /*
    RemoteServices.changePostPrivacy(post.id);
*/
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }
}
</script>

<style scoped></style>
