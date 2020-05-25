<template>
  <div>
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
    <div class="text-left ml-3 mt-3" v-if="comments.length === 0">{{
      "Currently there's no comments on this post. Be the first one by pressing the comment button."
    }}</div>
    <v-list v-if="comments.length !== 0">
      <v-card
        v-for="c in comments.filter(cc => cc.parent == null)"
        :key="c.id"
        outlined
        class="mx-auto"
      >
        <div class="headline grey--text font-weight-bold text-left ml-3">
          {{ c.user.username.concat(' wrote:') }}
        </div>
        <v-divider inset class="mt-3"></v-divider>
        <div class="text-left ml-5 mt-3">
          {{ c.comment }}
        </div>
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
        <div v-for="child in c.children" :key="child.id" class="mb-5">
          <v-divider></v-divider>
          <div class="headline grey--text font-weight-bold text-left ml-3">
            {{ c.user.username.concat(' replied:') }}
          </div>
          <v-divider inset class="mt-3"></v-divider>
          <div class="text-left ml-5 mt-3">
            {{ child.comment }}
          </div>
        </div>
      </v-card>
    </v-list>
  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
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
  @Model('typing-comment', Boolean) typingComment!: boolean;
  @Model('typing-reply', Boolean) typingReply!: boolean;
  comment: string = '';
  selectedComment: PostComment | null = null;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  writeReply(comment: PostComment) {
    if (comment === this.selectedComment || !this.typingReply)
      this.$emit('typing-reply');
    if (this.typingComment) this.$emit('typing-comment');
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
    if (this.typingComment) this.$emit('typing-comment');
    if (this.typingReply) this.$emit('typing-reply');
  }
}
</script>

<style></style>
