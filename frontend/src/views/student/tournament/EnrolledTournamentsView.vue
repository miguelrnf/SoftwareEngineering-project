<template>
  <div class="container">
    <h2>Enrolled Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Starts</div>
        <div class="col">Ends</div>
        <div class="col">Questions</div>
        <div class="col">Status</div>
        <div class="col">Assessment</div>
        <div class="col">Solved</div>
        <div class="col last-col"></div>
      </li>
      <li class="list-row" v-for="t in tournaments" :key="t.id">
        <div class="col" data-cy="title">
          {{ t.title }}
          <p v-show="false" data-cy="id">
            <span id="num"> {{ t.id }} </span>
          </p>
        </div>
        <div class="col">
          {{ t.availableDate }}
        </div>
        <div class="col">
          {{ t.conclusionDate }}
        </div>
        <div class="col">
          {{ t.numberOfQuestions }}
        </div>
        <div class="col">
          {{ t.status }}
        </div>
        <div class="col">
          {{ t.assessmentDto.title }}
        </div>
        <div v-if="t.status === 'OPEN'" class="col">
          {{ t.completed }}
        </div>
        <div v-else class="col">
          {{ t.completed }}
        </div>
        <div class="col">
          <v-btn
            v-if="isSolvable(t)"
            class="btn"
            color="primary"
            @click="solveQuiz(t.quiz)"
          >
            Solve
          </v-btn>
          <v-btn
            v-else-if="prepareToResults(t)"
            class="btn"
            color="primary"
            @click="showResults(t.solved)"
          >
            Results
          </v-btn>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';
import SolvedQuiz from '@/models/statement/SolvedQuiz';

@Component
export default class EnrolledTournamentsView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getEnrolledTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async solveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    await this.$router.push({ name: 'solve-quiz' });
  }

  async showResults(quiz: SolvedQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.correctAnswers = quiz.correctAnswers;
    statementManager.statementQuiz = quiz.statementQuiz;
    await this.$router.push({ name: 'quiz-results' });
  }

  prepareToResults(t: Tournament): boolean {
    return (
      t.completed && t.quiz?.timeToResults != null && t.quiz?.timeToResults <= 0
    );
  }

  isSolvable(t: Tournament): boolean {
    return t.status == 'OPEN' && !t.completed;
  }
}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
