<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="quizzes"
      :search="search"
      :sort-by="['creationDate']"
      sort-desc
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      style="cursor: pointer"
      @click:row="showResults"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon large class="mr-2" v-on="on" @click="showResults(item)"
              >fas fa-chevron-circle-right</v-icon
            >
          </template>
          <span>Show Results</span>
        </v-tooltip>
      </template>

      <template v-slot:item.title="{ item }">
        <p>
          {{ item.title }}
        </p>
      </template>

      <template v-slot:item.score="{ item }">
        <p>{{ calculateScore(item) }}</p>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';
import SolvedQuiz from '@/models/statement/SolvedQuiz';

@Component
export default class AvailableQuizzesView extends Vue {
  quizzes: SolvedQuiz[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '50px',
      sortable: false
    },
    { text: 'Title', value: 'statementQuiz.title', align: 'left' },
    {
      text: 'Solved Date',
      value: 'answerDate',
      align: 'left'
    },
    {
      text: 'Score',
      value: 'score',
      align: 'left'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.quizzes = (await RemoteServices.getSolvedQuizzes()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateScore(quiz: SolvedQuiz) {
    let correct = 0;
    for (let i = 0; i < quiz.statementQuiz.questions.length; i++) {
      if (
        quiz.statementQuiz.answers[i] &&
        quiz.correctAnswers[i].correctOptionId ===
          quiz.statementQuiz.answers[i].optionId
      ) {
        correct += 1;
      }
    }
    return `${correct}/${quiz.statementQuiz.questions.length}`;
  }

  async showResults(quiz: SolvedQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.correctAnswers = quiz.correctAnswers;
    statementManager.statementQuiz = quiz.statementQuiz;
    await this.$router.push({ name: 'quiz-results' });
  }
}
</script>

<style lang="scss">
.qrcode {
  width: 80vw !important;
  height: 80vw !important;
  max-width: 80vh !important;
  max-height: 80vh !important;
}
</style>
