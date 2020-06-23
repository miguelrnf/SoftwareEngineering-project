<template>
  <v-dialog
    scrollable
    :value="dialog"
    @input="$emit('close-show-post-dialog', false)"
    @keydown.esc="$emit('close-show-post-dialog', false)"
    class="post-dialog"
    max-width="90%"
  >
    <v-card>
      <v-app-bar dense color="primary">
        <v-toolbar-title class="white--text">{{
          post.question.question.title
        }}</v-toolbar-title>
        <div v-for="award in post.awards" :key="award">
          <v-badge
            class="font-weight-bold"
            offset-y="30"
            color=""
            :content="'x' + award.number"
            ><v-icon :color="award.award.item.color" small>{{
              award.award.item.icon
            }}</v-icon>
          </v-badge>
        </div>
        <v-spacer />
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-card
              color="accent"
              @click="
                awardsList.length === 0 ? buyAwardDialog() : buyAwardDialog()
              "
              v-on="on"
              class="px-1 mx-2"
            >
              <v-icon class="px-0" color="white">fas fa-award</v-icon>
            </v-card>
          </template>
          <span>Award this Post</span>
        </v-tooltip>
        <post-status-buttons :post="post"></post-status-buttons>
        <v-tooltip bottom v-if="isOwner(post)">
          <template v-slot:activator="{ on }">
            <v-icon color="white" small v-on="on" @click="editPost(post)"
              >edit</v-icon
            >
          </template>
          <span>Edit Post</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isTeacher() && post.answer != null">
          <template v-slot:activator="{ on }">
            <v-icon color="white" small v-on="on" @click="editAnswer(post)"
              >edit</v-icon
            >
          </template>
          <span>Edit Answer</span>
        </v-tooltip>
        <v-menu left bottom>
          <template v-slot:activator="{ on }">
            <v-btn icon v-on="on">
              <v-icon color="white">mdi-dots-vertical</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item v-for="n in 5" :key="n" @click="() => {}">
              <v-list-item-title>Option {{ n }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-app-bar>
      <show-post :post="post" />

      <v-app-bar dense color="primary" class="mt-3">
        <v-toolbar-title class="white--text"> {{ 'Comments' }}</v-toolbar-title>
        <v-spacer />
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="writeComment"
              data-cy="commentButton"
              color="white"
              >fas fa-comment</v-icon
            >
          </template>
          <span>Comment</span>
        </v-tooltip>
      </v-app-bar>
      <show-comments
        :comments="post.comments"
        :post="post"
        :typing-comment="typingComment"
        :typing-reply="typingReply"
        v-on:typing-comment="typingComment = !typingComment"
        v-on:typing-reply="typingReply = !typingReply"
      />
      <v-card-actions>
        <v-spacer />
        <v-btn
          v-if="post.answer == null && this.$store.getters.isTeacher"
          dark
          color="bprimary"
          @click="acceptAnswer = true"
          data-cy="answerPostButton"
          >answer</v-btn
        >
        <v-btn color="primary" text @click="$emit('close-show-post-dialog')"
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
    <buy-awards-dialog
      v-model="buyAwardsDialog"
      :post="post"
      :dialog="buyAwardsDialog"
      v-on:close-buy-awards-dialog="onCloseAwardDialog"
    />
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import ShowPost from '@/views/ShowPost.vue';
import AnswerPost from '@/views/AnswerPostDialog.vue';
import { PostAnswer } from '@/models/management/PostAnswer';
import RemoteServices from '@/services/RemoteServices';
import ShowComments from '@/views/ShowComments.vue';
import PostStatusButtons from '@/views/PostStatusButtons.vue';
import { PostAwardItem } from '@/models/management/PostAwardItem';
import BuyAwardsDialog from '@/views/BuyAwardsDialog.vue';

@Component({
  components: {
    'show-post': ShowPost,
    'answer-post': AnswerPost,
    'show-comments': ShowComments,
    'buy-awards-dialog': BuyAwardsDialog,
    'post-status-buttons': PostStatusButtons
  }
})
export default class ShowPostDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
  acceptAnswer: boolean = false;
  comment: string = '';
  editPostDialog: boolean = false;
  editAnswerDialog: boolean = false;
  buyAwardsDialog: boolean = false;
  typingComment: boolean = false;
  typingReply: boolean = false;
  awardsList: PostAwardItem[] = [];
  silversOnUser: number = 0;
  goldsOnUser: number = 0;
  platinumsOnUser: number = 0;

  async submitAnswer(answer: string) {
    if (answer != '') {
      this.post.answer = new PostAnswer();
      this.post.answer.teacherAnswer = answer;
      this.post.answer.user = this.$store.getters.getUser;
      this.post.answer.post = this.post;
      await RemoteServices.postAnswer(this.post);
    }
  }

  isOwner(post: Post): boolean {
    if (this.$store.getters.getUser != null) {
      return (
        this.$store.getters.getUser.username === post.question.user.username
      );
    } else return false;
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }

  editPost() {
    this.editPostDialog = true;
  }

  editAnswer() {
    this.editAnswerDialog = true;
  }

  writeComment() {
    this.typingComment = !this.typingComment;
    if (this.typingReply) this.typingReply = !this.typingReply;
  }

  buyAwardDialog() {
    this.buyAwardsDialog = true;
  }

  onCloseAwardDialog() {
    this.buyAwardsDialog = false;
  }

  async awardPost() {
    let user = await RemoteServices.updateLoggedUser();
    this.awardsList = await RemoteServices.getAwards();
    if (this.awardsList.length == 0) {
      return 'buy-awards-dialog';
    } else {
      for (let i = 0; i <= this.awardsList.length; i++) {
        if (this.awardsList[i].type == 'PLATINUM') {
          this.platinumsOnUser == this.platinumsOnUser + 1;
        }
        if (this.awardsList[i].type == 'GOLD') {
          this.goldsOnUser = this.goldsOnUser + 1;
        }
        if (this.awardsList[i].type == 'SILVER') {
          this.silversOnUser = this.silversOnUser + 1;
        }
      }
    }
  }
}
</script>
<style>
.post-dialog {
  position: absolute;
  top: 50px;
}
</style>
