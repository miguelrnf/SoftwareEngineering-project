<template>
  <v-card class="mx-auto" max-height="80%">
    <v-card-text>
      <div></div>
      <p class="subtitle-1 font-weight-light">
        <span v-html="convertMarkDown(post.question.question.title)" />
      </p>
      <p class="headline font-weight-black">
        <span v-html="convertMarkDown(post.question.question.content)" />
      </p>
      <div class="title text-left">
        <span v-html="convertMarkDown(post.question.studentQuestion)" />
      </div>
      <div class="text-right">
        by
        <span v-html="convertMarkDown(post.question.user.username)" />
      </div>
    </v-card-text>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';

@Component
export default class ShowPost extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
