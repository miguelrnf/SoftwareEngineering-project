<template>
  <div class="container">
    <v-card>
      <v-container>
        <v-card-title class="justify-center">Create Random Quiz</v-card-title>
      </v-container>
      <v-container>
        <p>Assessment</p>
        <v-btn-toggle
          v-model="statementManager.assessment"
          mandatory
          class="button-group"
        >
          <v-btn
            v-for="assessment in availableAssessments"
            text
            :value="assessment.id"
            :key="assessment.id"
            >{{ assessment.title }}</v-btn
          >
        </v-btn-toggle>
        <v-card-text v-if="availableAssessments.length === 0" class="message">
          {{ 'NO ASSESSMENTS FOUND' }}
        </v-card-text>
      </v-container>
      <v-container>
        <div>
          <p class="pl-0">Number of Questions</p>
          <v-btn-toggle
            v-model="statementManager.numberOfQuestions"
            mandatory
            class="button-group"
          >
            <v-btn text value="5">5</v-btn>
            <v-btn text value="10">10</v-btn>
            <v-btn text value="20" data-cy="twentyQuestionsButton">20</v-btn>
          </v-btn-toggle>
        </div>
      </v-container>
      <v-container>
        <div data-cy="quizButtons">
          <v-btn
            @click="createQuiz"
            depressed
            color="primary"
            data-cy="createQuizButton"
          >
            Create quiz
          </v-btn>
        </div>
      </v-container>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class CreateQuizzesView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  availableAssessments: Assessment[] = [];

  async created() {
    await this.$store.dispatch('loading');
    this.statementManager.reset();
    try {
      this.availableAssessments = await RemoteServices.getAvailableAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async createQuiz() {
    try {
      await this.statementManager.getQuizStatement();
      await this.$router.push({ path: '/student/quiz' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped>
.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>
