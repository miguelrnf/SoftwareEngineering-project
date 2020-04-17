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
        </v-card-title>
      </template>

      <template v-slot:item.title="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.question.question.title, null)"
          @click="showPostDialog(item)"
      /></template>

      <template v-slot:item.question="{ item }">
        <p
          v-html="
            convertMarkDownNoFigure(
              getSmallQuestion(item.question.studentQuestion),
              null
            )
          "
          @click="showPostDialog(item)"
      /></template>

      <template v-slot:item.user="{ item }">
        <p v-html="convertMarkDownNoFigure(item.question.user.username, null)"
      /></template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showPostDialog(item)"
              data-cy="showButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Post</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isOwner">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editPost(item)"
              data-cy="editButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Post</span>
        </v-tooltip>
        <v-tooltip bottom v-if="$store.getters.isTeacher">
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="redirectPost(item)"
              >cached</v-icon
            >
          </template>
          <span>Redirect Post</span>
        </v-tooltip>
        <v-tooltip bottom>
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
        <post-status-buttons :post="item"></post-status-buttons>
      </template>
    </v-data-table>
    <edit-post-dialog
      v-if="currentPost"
      v-model="editPostDialog"
      :post="currentPost"
      v-on:save-post="onSavePost"
      v-on:close-show-post-dialog="onCloseDialog"
    />
    <show-post-dialog
      v-if="currentPost"
      :dialog="postDialog"
      :post="currentPost"
      v-on:close-show-post-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Post from '@/models/management/Post';
import PostViewDialog from '@/views/PostViewDialog.vue';
import EditPostDialog from './EditPostDialog.vue';
import PostStatusButtons from '@/views/PostStatusButtons.vue';

@Component({
  components: {
    'show-post-dialog': PostViewDialog,
    'edit-post-dialog': EditPostDialog,
    'post-status-buttons': PostStatusButtons
  }
})
export default class PostsView extends Vue {
  posts: Post[] = [];
  filteredPosts: Post[] = [];
  totalPosts: number = 0;
  totalFilteredPosts: number = 0;
  currentPost: Post | null = null;
  editPostDialog: boolean = false;
  postDialog: boolean = false;
  search: string = '';
  perPage: number = 5;
  page: number = 1;
  discussStatus: boolean = false;
  postStatus: boolean = true;

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
        this.filteredPosts = res.lists;
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
    this.getPage();
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

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  showPostDialog(post: Post) {
    this.currentPost = post;
    this.postDialog = true;
  }

  onCloseDialog() {
    this.postDialog = false;
  }

  editPost(post: Post) {
    this.currentPost = post;
    this.editPostDialog = true;
  }

  isOwner(post: Post): boolean {
    return this.$store.getters.getUser.username === post.question.user.username;
  }

  redirectPost() {}

  async onSavePost(post: Post) {
    this.posts = this.posts.filter(p => p.id !== post.id);
    this.posts.unshift(post);
    this.editPostDialog = false;
    this.currentPost = null;
    this.postDialog = false;
  }

  async deletePost(post: Post) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.deletePost(post.id);
      await this.getPage();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
