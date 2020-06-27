<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-show-post-dialog', false)"
    @keydown.esc="$emit('close-show-post-dialog', false)"
    class="post-dialog ma-0"
    max-width="90%"
  >
    <v-card>
      <v-app-bar height="65%" dense color="primary" class="">
        <v-col cols="3" class="mx-n3">
          <v-toolbar-title class="white--text text-left my-n1">{{
            post.question.question.title
          }}</v-toolbar-title>
          <v-col cols="10" class="my-n2">
            <v-row justify="end" class="">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-progress-linear
                    class=""
                    color="primary lighten-4"
                    background-color="red lighten-4"
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
            </v-row>
          </v-col>
        </v-col>
        <div v-for="awards in post.awards" :key="awards.award.type">
          <v-badge
            v-if="awards.award.type === 'PLATINUM'"
            class="font-weight-bold"
            offset-y="34"
            offset-x="23"
            color=""
            :content="'x' + awards.number"
            ><v-icon class="px-3" :color="awards.award.item.color" medium>{{
              awards.award.item.icon
            }}</v-icon>
          </v-badge>
        </div>
        <div v-for="awards in post.awards" :key="awards.award.item.id">
          <v-badge
            v-if="awards.award.type === 'GOLD'"
            class="font-weight-bold"
            offset-y="30"
            offset-x="20"
            color=""
            :content="'x' + awards.number"
            ><v-icon class="px-3" :color="awards.award.item.color" small>{{
              awards.award.item.icon
            }}</v-icon>
          </v-badge>
        </div>
        <div v-for="awards in post.awards" :key="awards.award.item.name">
          <v-badge
            v-if="awards.award.type === 'SILVER'"
            class="font-weight-bold"
            offset-y="30"
            offset-x="20"
            color=""
            :content="'x' + awards.number"
            ><v-icon class="px-3" :color="awards.award.item.color" small>{{
              awards.award.item.icon
            }}</v-icon>
          </v-badge>
        </div>

        <v-spacer />
        <v-tooltip bottom v-if="!isTeacher()">
          <template v-slot:activator="{ on }">
            <v-card
              color="accent"
              @click="getUserAwards()"
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
    <edit-post-dialog
      v-if="post"
      v-model="editPostDialog"
      :post="post"
      v-on:close-edit-post-dialog="onCloseEditPost"
    />
    <buy-awards-dialog
      v-model="buyAwardsDialog"
      :post="post"
      :dialog="buyAwardsDialog"
      v-on:close-buy-awards-dialog="onCloseAwardDialog"
    />
    <award-post-dialog
      v-model="awardDialog"
      :post="post"
      :dialog="awardDialog"
      v-on:close-buy-awards-dialog="onCloseAwardDialog()"
      v-on:timeupdate="getNumberOfAwards()"
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
import AwardPostDialog from '@/views/AwardPostDialog.vue';
import { AwardsPerPost } from '@/models/management/AwardsPerPost';
import EditPostDialog from '@/views/EditPostDialog.vue';
import { ShopItem } from '@/models/management/ShopItem';

@Component({
  components: {
    'show-post': ShowPost,
    'answer-post': AnswerPost,
    'show-comments': ShowComments,
    'buy-awards-dialog': BuyAwardsDialog,
    'award-post-dialog': AwardPostDialog,
    'edit-post-dialog': EditPostDialog,
    'post-status-buttons': PostStatusButtons
  }
})
export default class ShowPostDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Post, required: true }) post!: Post;
  acceptAnswer: boolean = false;
  comment: string = '';
  editPostDialog: boolean = false;
  editAnswerDialog: boolean = false;
  buyAwardsDialog: boolean = false;
  awardDialog: boolean = false;
  typingComment: boolean = false;
  typingReply: boolean = false;
  awardsList: PostAwardItem[] = [];
  shopAwards: ShopItem[] = [];

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

  onCloseEditPost() {
    this.editPostDialog = false;
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

  awardPostDialog() {
    this.awardDialog = true;
  }

  onCloseAwardDialog() {
    this.buyAwardsDialog = false;
    this.awardDialog = false;
  }

  async getUserAwards() {
    this.awardsList = await RemoteServices.getAwards();
    this.awardsList.length === 0
      ? this.buyAwardDialog()
      : this.awardPostDialog();
  }

  async created() {
    this.shopAwards = await RemoteServices.getShopItems();
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
<style>
.post-dialog {
  position: absolute;
  top: 50px;
}
</style>
