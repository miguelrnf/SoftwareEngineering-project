<template>
  <v-card class="mx-auto" max-height="80%">
    <v-app-bar dense color="grey lighten-2">
      <v-toolbar-title> {{ 'Comments' }}</v-toolbar-title>
      <v-spacer />
      <v-tooltip bottom>
        <template v-slot:activator="{ on }">
          <v-icon
            class="mr-2"
            v-on="on"
            @click="writeComment"
            data-cy="commentButton"
            >fas fa-comment</v-icon
          >
        </template>
        <span>Comment</span>
      </v-tooltip>
    </v-app-bar>
    <v-textarea
      v-if="typingComment"
      filled
      color="grey"
      no-resize
      name="comment input"
      label="Type a comment"
      v-model="comment"
      @keydown.enter.exact="submitComment"
      data-cy="commentBox"
    ></v-textarea>
    <v-card-text v-if="comments.length === 0">{{
      "Currently there's no comments on this post. Be the first one by pressing the comment button."
    }}</v-card-text>
    <v-list v-if="comments.length !== 0">
      <v-card
        v-for="c in comments.filter(cc => cc.parent == null)"
        :key="c.id"
        outlined
        class="mx-auto"
      >
        <v-card-title>{{ c.user.username.concat(' wrote:') }}</v-card-title>
        <v-card-text>
          <span v-html="convertMarkDown(c.comment)" />
        </v-card-text>
        <!--For the time being you cannot reply to a comment that just got posted since it has no id-->
        <v-card-actions v-if="c.id">
          <v-spacer />
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon v-on="on" @click="writeReply(c)" data-cy="replyButton"
                >fas fa-reply</v-icon
              >
            </template>
            <span>Reply</span>
          </v-tooltip>
        </v-card-actions>
        <v-textarea
          v-if="typingReply && selectedComment === c"
          filled
          color="grey"
          no-resize
          name="comment input"
          label="Type a comment"
          v-model="comment"
          @keydown.enter.exact="submitComment"
          data-cy="replyBox"
        ></v-textarea>
        <v-card-text v-for="child in c.children" :key="child.id">
          <v-divider></v-divider>
          <v-card-title>{{ c.user.username.concat(' replied:') }}</v-card-title>
          <v-card-text v-html="convertMarkDown(child.comment)" />
        </v-card-text>
      </v-card>
    </v-list>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import { PostComment } from '@/models/management/PostComment';
import RemoteServices from '@/services/RemoteServices';
import Post from '@/models/management/Post';

@Component({
  components: {}
})
export default class ShowComments extends Vue {
  @Prop({ type: Array, required: true })
  readonly comments!: PostComment[];
  @Prop({ type: Post, required: true })
  readonly post!: Post;
  typingComment: boolean = false;
  typingReply: boolean = false;
  comment: string = '';
  selectedComment: PostComment | null = null;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  writeComment() {
    this.typingComment = !this.typingComment;
    if (this.typingReply) this.typingReply = !this.typingReply;
  }

  writeReply(comment: PostComment) {
    if (comment === this.selectedComment || !this.typingReply)
      this.typingReply = !this.typingReply;
    if (this.typingComment) this.typingComment = !this.typingComment;
    this.selectedComment = comment;
  }

  submitComment() {
    if (this.comment.trim() != '') {
      let pc = new PostComment();
      pc.comment = this.comment;
      pc.user = this.$store.getters.getUser;
      pc.post.id = this.post.id;
      pc.creationDate = '';
      pc.parent = null;
      if (this.typingReply) {
        pc.parent = new PostComment();
        if (this.selectedComment != null) {
          pc.parent.id = this.selectedComment.id;
          this.selectedComment.children.push(pc);
        }
      }
      this.comments.push(pc);
      RemoteServices.writeComment(pc);
    }
    this.comment = '';
    this.typingComment = false;
    this.typingReply = false;
  }
}
</script>

<style></style>
