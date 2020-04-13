<template>
  <v-container grid-list-md fluid>
    <v-layout column wrap>
      <ol>
        <li
                v-for="post in allp"
                :id="post.id"
                class="text-left"
        >
        </li>
      </ol>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';

@Component({})
export default class PostsView extends Vue {
  @Prop({ type: Array, required: true }) readonly allp!: Post[];
}
</script>
