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
        <v-btn dark color="blue darken-1" @click="$emit('close-show-post-dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';
import ShowPost from '@/views/ShowPost.vue';

@Component({
  components: {
    'show-post': ShowPost
  }
})
export default class PostViewDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Post, required: true }) readonly post!: Post;
}
</script>
