<template>
  <v-dialog
    scrollable
    :value="dialog"
    @input="$emit('close-posts-by-quiz-dialog', false)"
    @keydown.esc="$emit('close-posts-by-quiz-dialog', false)"
  >
    <v-card class="mt-7" v-if="posts.length !== 0">
      <div>
        <post-preview
          v-for="p in posts"
          :key="p.id"
          :post="p"
          @click.native="showPostOpenDialog(p)"
        >
        </post-preview>
      </div>
    </v-card>
    <v-card v-if="posts.length === 0">
      <v-card-text class="mt-5"
        >There are no posts for the questions in this quiz :(</v-card-text
      >
    </v-card>
    <show-post-dialog
      v-if="currentPost"
      :dialog="postDialog"
      :post="currentPost"
      v-on:close-show-post-dialog="onCloseDialog"
    />
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '../models/management/Post';
import PostPreview from '@/views/PostPreview.vue';
import PostViewDialog from '@/views/ShowPostDialog.vue';

@Component({
  components: {
    'post-preview': PostPreview,
    'show-post-dialog': PostViewDialog
  }
})
export default class PostsByQuizDialog extends Vue {
  @Prop({ type: Array, required: true }) readonly posts!: Post[];
  @Prop({ type: Boolean, required: true }) readonly dialog!: Boolean;
  currentPost: Post | null = null;
  postDialog: boolean = false;

  showPostOpenDialog(post: Post) {
    this.currentPost = post;
    this.postDialog = true;
  }

  onCloseDialog() {
    this.postDialog = false;
  }
}
</script>

<style scoped></style>
