<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-answer-post-dialog', false)"
    @keydown.esc="$emit('close-answer-post-dialog', false)"
    max-width="40%"
  >
    <v-card>
      <v-textarea
        class="box-part"
        :counter="maxSize"
        filled
        label="TYPE YOUR ANSWER"
        v-model="answer"
        @keydown.enter.exact="
          $emit('close-post-answered-dialog', answer) &&
            $emit('close-answer-post-dialog', false)
        "
        data-cy="typeAnswer"
      ></v-textarea>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Post from '@/models/management/Post';

@Component
export default class AnswerPostDialog extends Vue {
  @Prop({ type: Post, required: true }) readonly post!: Post;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  maxSize: number = 1024;
  answer: string = '';
}
</script>

<style>
.box-part {
  border-radius: 10px;
}
</style>
