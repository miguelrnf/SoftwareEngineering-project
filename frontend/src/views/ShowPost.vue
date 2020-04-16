<template>
  <v-card class="mx-auto" max-height="80%">
    <v-app-bar dense color="grey lighten-2">
      <v-toolbar-title>
        {{ convertMarkDown(post.question.question.title) }}</v-toolbar-title
      >
      <v-spacer />
      <post-status-buttons :post="post"></post-status-buttons>
      <v-menu
              left
              bottom
      >
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>

        <v-list>
          <v-list-item
                  v-for="n in 5"
                  :key="n"
                  @click="() => {}"
          >
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
    <br />
    <v-card-text class="box-part" v-if="post.answer != null && post.answer.teacherAnswer !== ''">
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';
import PostStatusButtons from '@/views/PostStatusButtons.vue';

@Component({
  components: {
    'post-status-buttons': PostStatusButtons
  }
})
export default class ShowPost extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

}
</script>

<style>
.box-part {
  border-radius: 10px;
  border: 2px solid dimgrey;
}
</style>