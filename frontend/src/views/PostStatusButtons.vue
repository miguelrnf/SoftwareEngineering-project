<template>
  <div>
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.discussStatus) === 'red' &&
          isOwner(post) &&
          post.answer != null
      "
      :color="getColor(post.discussStatus)"
      dark
      @click="changeDiscussStatus(post)"
      data-cy="DiscussStatusButton"
      >{{ 'Unresolved' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.discussStatus) === 'green' &&
          isOwner(post) &&
          post.answer != null
      "
      :color="getColor(post.discussStatus)"
      dark
      @click="changeDiscussStatus(post)"
      data-cy="DiscussStatusButton"
      >{{ 'Resolved' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.discussStatus) === 'red' &&
          (!isOwner(post) || post.answer == null)
      "
      :color="getColor(post.discussStatus)"
      dark
      >{{ 'Unresolved' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor(post.discussStatus) === 'green' &&
          (!isOwner(post) || post.answer == null)
      "
      :color="getColor(post.discussStatus)"
      dark
      >{{ 'Resolved' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="getColor2(post.postStatus) === 'blue' && isTeacher()"
      :color="getColor2(post.postStatus)"
      dark
      @click="changePostStatus(post)"
      data-cy="PostStatusButton"
      >{{ 'Open' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="getColor2(post.postStatus) === 'grey' && isTeacher()"
      :color="getColor2(post.postStatus)"
      dark
      @click="changePostStatus(post)"
      data-cy="PostStatusButton"
      >{{ 'Closed' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="getColor2(post.postStatus) === 'grey' && !isTeacher()"
      :color="getColor2(post.postStatus)"
      dark
      >{{ 'Closed' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="getColor2(post.postStatus) === 'blue' && !isTeacher()"
      :color="getColor2(post.postStatus)"
      dark
      >{{ 'Open' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor3(post.postPrivacy) === 'black' &&
          (isOwner(post) || isTeacher())
      "
      :color="getColor3(post.postPrivacy)"
      dark
      @click="changePostPrivacy(post)"
      data-cy="PostPrivacyButton"
      >{{ 'Private' }}</v-chip
    >
    <v-chip
      class="ma-2"
      small
      label
      v-if="
        getColor3(post.postPrivacy) === 'orange' &&
          (isOwner(post) || isTeacher())
      "
      :color="getColor3(post.postPrivacy)"
      dark
      @click="changePostPrivacy(post)"
      data-cy="PostPrivacyButton"
      >{{ 'Public' }}</v-chip
    >
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '../models/management/Post';
import RemoteServices from '../services/RemoteServices';

@Component
export default class PostStatusButtons extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;

  getColor(dStatus: boolean) {
    if (dStatus) return 'green';
    else return 'red';
  }

  isOwner(post: Post): boolean {
    if (this.$store.getters.getUser != null) {
      return (
        this.$store.getters.getUser.username === post.question.user.username
      );
    } else return false;
  }

  getColor2(pStatus: boolean) {
    if (pStatus) return 'blue';
    else return 'grey';
  }

  getColor3(privacy: boolean) {
    if (privacy) return 'black';
    else return 'orange';
  }

  changeDiscussStatus(post: Post) {
    if (this.isOwner(post)) post.discussStatus = !post.discussStatus;
    RemoteServices.changeDiscussStatus(post.id);
  }

  changePostStatus(post: Post) {
    if (this.isTeacher()) post.postStatus = !post.postStatus;
    RemoteServices.changePostStatus(post.id);
  }

  changePostPrivacy(post: Post) {
    if (this.isOwner(post) || this.isTeacher())
      post.postPrivacy = !post.postPrivacy;
    RemoteServices.changePostPrivacy(post.id);
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }
}
</script>

<style scoped></style>
