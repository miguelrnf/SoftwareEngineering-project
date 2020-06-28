<template>
  <v-card max-height="350" min-height="100" outlined hover>
    <v-row class="mx-0">
      <v-card-title class="pa-2">{{
        post.question.question.title
      }}</v-card-title>
      <v-spacer />
      <div class="pa-2">
        <v-chip
          class="ma-1"
          x-small
          label
          :color="getColor1(post.discussStatus)"
          dark
        />
        <v-chip
          class="ma-1"
          x-small
          label
          :color="getColor2(post.postStatus)"
          dark
        />
        <v-chip
          class="ma-1"
          x-small
          label
          :color="getColor3(post.postPrivacy)"
          dark
        />
      </div>
    </v-row>
    <div class="mt-n4 text-left">
      <v-card-subtitle>Question:</v-card-subtitle>
      <v-card-text class="mt-n3">{{
        post.question.studentQuestion
      }}</v-card-text>
    </div>
    <v-row class="mx-0">
      <v-spacer />
      <div>
        <v-chip
          class="ma-1"
          small
          label
          :color="getAnsweredColor(post.answer)"
          dark
          >{{
            post.answer != null ? 'Answered!' : 'Not Answered yet :('
          }}</v-chip
        >
        <v-chip class="ma-1" small label color="grey" dark>{{
          post.comments.length !== 1
            ? post.comments.length + ' comments'
            : post.comments.length + ' comment'
        }}</v-chip>
      </div>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';
import { PostAnswer } from '@/models/management/PostAnswer';

@Component
export default class PostPreview extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getColor1(dStatus: boolean) {
    if (dStatus) return 'green';
    else return 'red';
  }

  getColor2(pStatus: boolean) {
    if (pStatus) return 'blue';
    else return 'grey';
  }

  getColor3(pStatus: boolean) {
    if (pStatus) return 'black';
    else return 'orange';
  }

  getAnsweredColor(answer: PostAnswer) {
    if (answer != null) return 'green lighten-1';
    else return 'red lighten-1';
  }
}
</script>
<style scoped />
