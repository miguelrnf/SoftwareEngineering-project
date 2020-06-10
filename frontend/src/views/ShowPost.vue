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
    <v-divider inset class="mt-3"></v-divider>
    <v-card-text>
      <template>
        <div class="text-right my-5">
          <v-btn class="ma-2" @click="upvote()" icon>
            <v-icon :color="getColorOfUpvotes()" class="mx-4" large left
              >fas fa-chevron-up</v-icon
            >
            <span
              class="font-weight-bold subtitle-1"
              v-html="post.upvotes - post.downvotes"
            />
          </v-btn>
          <v-btn class="ma-2" @click="downvote()" icon>
            <v-icon class="mx-4" :color="getColorOfDownvotes()" large right
              >fas fa-chevron-down</v-icon
            >
          </v-btn>
        </div>
        <div>
          <v-row justify="end">
            <v-col cols="2">
              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <v-progress-linear
                    color="light-blue"
                    height="10"
                    :value="valueForProgress()"
                    striped
                    rounded
                    v-on="on"
                  ></v-progress-linear>
                </template>
                <span>
                  {{ 'Up: ' + this.post.upvotes }}
                  {{ ' Down: ' + this.post.downvotes }}
                </span>
              </v-tooltip>
            </v-col>
          </v-row>
        </div>
      </template>

      <div class="text-right">
        by
        <span v-html="convertMarkDown(post.question.user.username)" />
      </div>
      <v-card-text
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
    </v-card-text>
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
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'edit-post-dialog': EditPostDialog,
    'edit-answer-dialog': EditAnswerDialog,
    'post-status-buttons': PostStatusButtons
  }
})
export default class ShowPost extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;
  newPost: Post = new Post();
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

  getColor3(privacy: boolean) {
    if (privacy) return 'black';
    else return 'orange';
  }

  isOwnerAnswer(post: Post): boolean {
    if (post.answer != null)
      return this.$store.getters.getUser.username === post.answer.user.username;
    else return false;
  }

  getColorOfUpvotes() {
    if (this.$store.getters.getUser.postsUpvoted != null) {
      for (
        let i = 0;
        i < this.$store.getters.getUser.postsUpvoted.length;
        i++
      ) {
        if (this.$store.getters.getUser.postsUpvoted[i].id == this.post.id) {
          return 'blue';
        }
      }
    } else return 'grey';
  }

  getColorOfDownvotes() {
    if (this.$store.getters.getUser.postsDownvoted != null) {
      for (
        let i = 0;
        i < this.$store.getters.getUser.postsDownvoted.length;
        i++
      ) {
        if (this.$store.getters.getUser.postsDownvoted[i].id == this.post.id) {
          return 'blue';
        }
      }
    } else return 'grey';
  }

  async updateVotes() {
    await this.$store.dispatch('updateVotes');
  }

  async upvote() {
    let post2 = await RemoteServices.vote(this.post.id, 'upvote');
    this.post.upvotes = post2.upvotes;
    this.post.downvotes = post2.downvotes;
    await this.updateVotes();
  }

  async downvote() {
    let post2 = await RemoteServices.vote(this.post.id, 'downvote');
    this.post.upvotes = post2.upvotes;
    this.post.downvotes = post2.downvotes;
    await this.updateVotes();
  }

  valueForProgress() {
    if (this.post.downvotes == 0 && this.post.upvotes != 0) {
      return 100;
    }
    if (this.post.upvotes == 0) {
      return 0;
    } else {
      let upvotes = this.post.upvotes;
      let downvotes = this.post.downvotes;
      return (upvotes / (upvotes + downvotes)) * 100;
    }
  }
}
</script>

<style></style>
