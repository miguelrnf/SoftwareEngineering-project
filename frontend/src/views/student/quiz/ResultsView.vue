<template>
  <v-card max-height="250%" min-height="100%" class="quiz-container overflow-x-hidden" v-if="statementManager.correctAnswers.length > 0">
    <v-card height="45px" class="question-navigation">
      <v-row :justify="'center'" class="navigation-buttons">
        <v-col
          md="auto"
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="'question-button'"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          <v-card tile :color="color(index)">
            {{ index }}
          </v-card>
        </v-col>
      </v-row>
      <v-icon
        class="left-button"
        @click="decreaseOrder"
        color="secondary"
        x-large
        v-if="questionOrder !== 0"
        >fas fa-chevron-left</v-icon
      >
      <v-icon
        class="right-button"
        @click="increaseOrder"
        color="secondary"
        x-large
        v-if="
          questionOrder !== statementManager.statementQuiz.questions.length - 1
        "
        >fas fa-chevron-right</v-icon
      >
    </v-card>
    <v-row justify="end">
      <v-chip
        class="mr-12 mb-n10 mt-2"
        color="primary"
        text-color="white"
        @click="openPostsDialog"
        data-cy="checkPostsByQuizButton"
        >Check posts for this quiz</v-chip
      >
    </v-row>
    <result-component
      v-model="questionOrder"
      :answer="statementManager.statementQuiz.answers[questionOrder]"
      :correctAnswer="statementManager.correctAnswers[questionOrder]"
      :question="statementManager.statementQuiz.questions[questionOrder]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    />
    <posts-by-quiz
      v-if="postsDialog"
      :dialog="postsDialog"
      :posts="quizPosts"
      v-on:close-posts-by-quiz-dialog="onCloseDialog"
    >
    </posts-by-quiz>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import Post from '@/models/management/Post';
import RemoteServices from '@/services/RemoteServices';
import PostsByQuiz from '@/views/PostsByQuizDialog.vue';

@Component({
  components: {
    'result-component': ResultComponent,
    'posts-by-quiz': PostsByQuiz
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  quizPosts: Post[] = [];
  postsDialog: boolean = false;

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    } else if (this.statementManager.correctAnswers.length === 0) {
      await this.$store.dispatch('loading');
      setTimeout(() => {
        this.statementManager.concludeQuiz();
      }, 2000);

      await this.$store.dispatch('clearLoading');
    }
    if (this.statementManager.statementQuiz != null) {
      let res = await RemoteServices.postsByQuiz(
        this.statementManager.statementQuiz.id
      );
      this.quizPosts = res.lists;
    }
  }

  color(index: number): String {
    if (index === this.questionOrder + 1) {
      if (
        this.statementManager.correctAnswers[index - 1].correctOptionId !==
        this.statementManager.statementQuiz?.answers[index - 1].optionId
      ) {
        return this.$vuetify.theme.dark ? 'error darken-4' : 'error lighten-2';
      }
      return this.$vuetify.theme.dark
        ? 'success darken-4'
        : 'success lighten-2';
    } else if (
      this.statementManager.correctAnswers[index - 1].correctOptionId !==
      this.statementManager.statementQuiz?.answers[index - 1].optionId
    ) {
      return 'error darken-2';
    }
    return 'success darken-2';
  }

  increaseOrder(): void {
    if (
      this.questionOrder + 1 <
      +this.statementManager.statementQuiz!.questions.length
    ) {
      this.questionOrder += 1;
    }
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.questionOrder -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.questionOrder = n;
    }
  }

  openPostsDialog(): void {
    this.postsDialog = true;
  }

  onCloseDialog(): void {
    this.postsDialog = false;
  }
}
</script>

<style lang="scss" scoped>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>
