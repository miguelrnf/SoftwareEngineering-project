<template>
  <div>
    <span v-html="convertMarkDown(question.content, question.image)" />
    <br />
    <ul>
      <li v-for="option in question.options" :key="option.number">
        <span
          v-if="option.correct"
          v-html="convertMarkDown('**[★]** ' + option.content)"
          v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
        <span v-else v-html="convertMarkDown(option.content)" />
      </li>
    </ul>
    <span v-if="showHint" v-html="convertMarkDown('Hint: ' + question.hint)" />
    <span v-else v-html="convertMarkDown('Hint: No hint was given')" />
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';

@Component
export default class ShowQuestion extends Vue {
  @Prop({ type: Question, required: true }) readonly question!: Question;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
  get showHint(): Boolean {
    if (this.question.hint == null) return false;
    else return this.question.hint.trim() !== '';
  }
}
</script>
