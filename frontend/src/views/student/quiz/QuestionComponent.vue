<template>
  <div class="question-container" v-if="question && question.options">
    <div class="question">
      <v-card
        v-if="backsies"
        class="square"
        color="accent darken-2"
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
      >
        <v-icon color="secondary" x-large v-if="hover && questionOrder !== 0"
          >fas fa-chevron-left</v-icon
        >
        <span v-else>{{ questionOrder + 1 }}</span>
      </v-card>
      <v-card class="question-content" color="secondary">
        <div v-html="convertMarkDown(question.content, question.image)" />
      </v-card>
      <v-card class="square" color="accent darken-2" @click="increaseOrder">
        <v-icon
          color="secondary"
          x-large
          v-if="questionOrder !== questionNumber - 1"
        >
          fas fa-chevron-right
        </v-icon>
      </v-card>
    </div>
    <div class="option-list">
      <v-row
        class="ma-0"
        v-for="(n, index) in question.options.length"
        :key="index"
        @click="selectOption(question.options[index].optionId)"
        v-bind:class="'option'"
      >
        <v-card class="square" :color="color(optionId, index)">
          <span class="option-letter">{{ optionLetters[index] }}</span>
        </v-card>
        <v-card
          class="option-content"
          :color="
            optionId === question.options[index].optionId ? 'secondary' : ''
          "
        >
          <div v-html="convertMarkDown(question.options[index].content)" />
        </v-card>
      </v-row>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Model, Prop, Vue } from 'vue-property-decorator';
import StatementQuestion from '@/models/statement/StatementQuestion';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';

@Component
export default class QuestionComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestion) readonly question: StatementQuestion | undefined;
  @Prop(Number) optionId: number | undefined;
  @Prop() readonly questionNumber!: number;
  @Prop() readonly backsies!: boolean;
  hover: boolean = false;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  @Emit()
  selectOption(optionId: number) {
    return optionId;
  }

  color(optionId: number, index: number): String {
    if (optionId === this.question?.options[index]?.optionId) {
      if (this.$vuetify.theme.dark) {
        return 'accent lighten-2';
      }
      return 'accent darken-2';
    }
    return 'accent';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped />
