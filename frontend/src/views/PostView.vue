<template>
  <p>Hi</p>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class PostView extends Vue {
  question: string = 'This is a question';
  studentQ: string = 'I am asking another question';
  answer: string = '';
  postStatus: boolean = true;
  discussStatus: boolean = false;
}
</script>

<style scoped></style>
