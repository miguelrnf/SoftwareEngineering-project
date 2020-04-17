<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-show-post-dialog', false)"
    @keydown.esc="$emit('close-show-post-dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ '\n' }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-post :post="post" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          v-if="post.answer == null && this.$store.getters.isTeacher"
          dark
          color="blue darken-1"
          @click="acceptAnswer = true"
          data-cy="answerPostButton"
          >answer</v-btn
        >
        <v-btn
          dark
          color="blue darken-1"
          @click="$emit('close-show-post-dialog')"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
    <answer-post
      v-if="acceptAnswer"
      :post="post"
      :dialog="acceptAnswer"
      v-on:close-answer-post-dialog="acceptAnswer = false"
      v-on:close-post-answered-dialog="submitAnswer"
    >
    </answer-post>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import ShowPost from '@/views/ShowPost.vue';
import AnswerPost from '@/views/AnswerPostDialog.vue';
import { PostAnswer } from '@/models/management/PostAnswer';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'show-post': ShowPost,
    'answer-post': AnswerPost
  }
})
export default class PostViewDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
  acceptAnswer: boolean = false;

  async submitAnswer(answer: string) {
    if (answer != '') {
      this.post.answer = new PostAnswer();
      this.post.answer.teacherAnswer = answer;
      this.post.answer.user = this.$store.getters.getUser;
      this.post.answer.post = this.post;
      await RemoteServices.postAnswer(this.post);
    }
  }
}
</script>
