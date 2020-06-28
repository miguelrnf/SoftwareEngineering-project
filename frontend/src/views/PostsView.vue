<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items.sync="filteredPosts"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page.sync="perPage"
      :page.sync="page"
      :server-items-length="totalFilteredPosts"
      @update:page="getPage"
      @update:items-per-page="getPage"
      :discuss-status.sync="discussStatus"
      :post-status.sync="postStatus"
      :post-privacy.sync="postPrivacy"
      :answer-privacy.sync="answerPrivacy"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model.lazy="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            @input="customFilter"
          />
          <v-spacer />

          <v-btn
            v-if="!isTeacher()"
            color="primary"
            dark
            @click="newPost"
            data-cy="createButton"
            >New Post</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.title="{ item }">
        <p
          v-html="convertMarkDown(item.question.question.title, null)"
          @click="showPostOpenDialog(item)"
        />
      </template>

      <template v-slot:item.question="{ item }">
        <p
          v-html="
            convertMarkDown(
              getSmallQuestion(item.question.studentQuestion),
              null
            )
          "
          @click="showPostOpenDialog(item)"
        />
      </template>

      <template v-slot:item.user="{ item }">
        <p v-html="convertMarkDown(item.question.user.username, null)" />
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showPostOpenDialog(item)"
              data-cy="showButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Post</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isOwner(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editPostOpenDialog(item)"
              data-cy="editButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Post</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isTeacher() && item.answer !== null">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editAnswerOpenDialog(item)"
              data-cy="editAnswerButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Answer</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isOwner(item) || isTeacher()">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="deletePost(item)"
              color="red"
              data-cy="deleteButton"
              >delete</v-icon
            >
          </template>
          <span>Delete Post</span>
        </v-tooltip>
      </template>
      <template v-slot:item.status="{ item }">
        <post-status-buttons :post="item" data-cy="StatusButtons" />
      </template>
    </v-data-table>
    <edit-post-dialog
      v-if="currentPost"
      v-model="editPostDialog"
      :post="currentPost"
      v-on:save-post-edit="onSavePost"
      v-on:close-edit-post-dialog="onCloseDialog"
    />
    <edit-answer-dialog
      v-if="currentPost && currentPost.answer"
      v-model="editAnswerDialog"
      :post="currentPost"
      v-on:save-post-edit-answer="onSavePost"
      v-on:close-edit-answer-dialog="onCloseDialog"
    />
    <show-post-dialog
      v-if="currentPost"
      :dialog="postDialog"
      :post="currentPost"
      v-on:save-post="onSavePost"
      v-on:close-show-post-dialog="onCloseDialog"
    />
    <create-post
      v-if="createPost"
      :dialog="createPost"
      v-on:save-post="onCreatePost"
      v-on:close-new-post-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';
import PostViewDialog from '@/views/ShowPostDialog.vue';
import EditPostDialog from './EditPostDialog.vue';
import PostStatusButtons from '@/views/PostStatusButtons.vue';
import EditAnswerDialog from '@/views/teacher/EditAnswerDialog.vue';
import AnswerPostDialog from '@/views/AnswerPostDialog.vue';
import NewPostView from '@/views/NewPostView.vue';

@Component({
  components: {
    'show-post-dialog': PostViewDialog,
    'edit-post-dialog': EditPostDialog,
    'edit-answer-dialog': EditAnswerDialog,
    'post-status-buttons': PostStatusButtons,
    'answer-post-dialog': AnswerPostDialog,
    'create-post': NewPostView
  }
})
export default class PostsView extends Vue {
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  totalPosts: number = 0;
  totalFilteredPosts: number = 0;
  currentPost: Post | null = null;
  editPostDialog: boolean = false;
  editAnswerDialog: boolean = false;
  postDialog: boolean = false;
  createPost: boolean = false;
  search: string = '';
  perPage: number = 5;
  page: number = 1;
  discussStatus: boolean = false;
  postStatus: boolean = true;
  postPrivacy: boolean = false;
  answerPrivacy: boolean = false;
  numberOfComments: number = 0;

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'question', align: 'center' },
    { text: 'User', value: 'user', align: 'center' },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    },
    { text: 'Status', value: 'status', align: 'center' }
  ];

  @Watch('editPostDialog')
  closeError() {
    if (!this.editPostDialog) {
      this.currentPost = null;
    }
  }

  async getPage() {
    await this.$store.dispatch('loading');
    try {
      let res = await RemoteServices.viewPosts(this.perPage, this.page);
      if (res.lists != undefined) {
        this.posts = res.lists;
        this.posts = this.posts.filter(
          post => this.isOwner(post) || !post.postPrivacy || this.isTeacher()
        );
        this.filteredPosts = this.posts;
      }
      if (res.totalPosts != undefined) {
        this.totalPosts = res.totalPosts;
        this.totalFilteredPosts = res.totalPosts;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async created() {
    await this.getPage();
  }

  //Pagination is done server-side --> can only search for posts in same page
  async customFilter(search: string) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    if (search != '') {
      this.filteredPosts = this.posts.filter(
        post =>
          JSON.stringify(
            post.question.question?.title +
              post.question.studentQuestion +
              post.question.user.username
          )
            .toLowerCase()
            .indexOf(search.toLowerCase()) !== -1
      );
      this.totalFilteredPosts = this.filteredPosts.length;
    } else {
      this.filteredPosts = this.posts;
      this.totalFilteredPosts = this.totalPosts;
    }
  }

  getSmallQuestion(question: string): string {
    if (question.length > 30) {
      return question.slice(0, 30).concat(' . . .');
    } else {
      return question;
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  showPostOpenDialog(post: Post) {
    this.currentPost = post;
    this.postDialog = true;
  }

  onCloseDialog() {
    this.postDialog = false;
    this.editPostDialog = false;
    this.editAnswerDialog = false;
    this.createPost = false;
  }

  newPost() {
    this.createPost = true;
  }

  editPostOpenDialog(post: Post) {
    this.currentPost = post;
    this.editPostDialog = true;
  }

  editAnswerOpenDialog(post: Post) {
    this.currentPost = post;
    this.editAnswerDialog = true;
  }

  isOwner(post: Post): boolean {
    if (this.$store.getters.getUser != null) {
      return (
        this.$store.getters.getUser.username === post.question.user.username
      );
    } else return false;
  }

  redirectPost() {}

  onSavePost(post: Post) {
    this.posts = this.posts.filter(p => p.id !== post.id);
    this.posts.unshift(post);
    this.createPost = false;
    this.editPostDialog = false;
    this.editAnswerDialog = false;
    this.currentPost = null;
  }

  onCreatePost(postmalone: Post) {
    this.posts.push(postmalone);
    this.createPost = false;
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }

  async deletePost(post: Post) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.deletePost(post.id);
      if (this.perPage != -1) {
        if (this.posts.length == 1 && this.page > 1) this.page = this.page - 1;
        await this.getPage();
      } else {
        this.posts = this.posts.filter(p => p.id != post.id);
        this.filteredPosts = this.posts;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  numberOfCommentsOnPost() {
    if (this.currentPost != null) {
      if (this.currentPost.comments != null) {
        return this.currentPost.comments.length;
      }
    }
    return;
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
