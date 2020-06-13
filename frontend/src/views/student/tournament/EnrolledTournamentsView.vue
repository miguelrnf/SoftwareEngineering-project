<template>
  <v-card class="table">
    <v-card-title style="font-size: xx-large"
      >Enrolled Tournaments</v-card-title
    >
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      :mobile-breakpoint="0"
      sort-desc
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
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

      <template v-slot:item.solved="{ item }">
        <v-tooltip v-if="item.completed === true" bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" color="success">fas fa-check</v-icon>
          </template>
          <span>Solved</span>
        </v-tooltip>
        <v-tooltip v-else bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" color="error">fas fa-times</v-icon>
          </template>
          <span>Not solved</span>
        </v-tooltip>
      </template>

      <template v-slot:item.action="{ item }">
        <v-btn
          v-if="isSolvable(item)"
          class="mr-2"
          color="primary"
          @click="solveQuiz(item.quiz)"
          >Solve
        </v-btn>
        <v-btn
          v-else-if="prepareToResults(item)"
          data-cy="details"
          class="mr-2"
          color="primary"
          @click="showResults(item)"
          >Results
        </v-btn>
      </template>
      <template v-slot:item.title="{ item }">
        <p v-html="convertMarkDown(item.title, null)" />
      </template>
      <template v-slot:item.availableDate="{ item }">
        <p>{{ item.availableDate }}</p>
      </template>
      <template v-slot:item.conclusionDate="{ item }">
        <p>{{ item.conclusionDate }}</p>
      </template>
      <template v-slot:item.numberOfQuestions="{ item }">
        <p>{{ item.numberOfQuestions }}</p>
      </template>
      <template v-slot:item.status="{ item }">
        <p v-html="convertMarkDown(item.status, null)" />
      </template>
      <template v-slot:item.assessmentDto.title="{ item }">
        <p v-html="convertMarkDown(item.assessmentDto.title, null)" />
      </template>
    </v-data-table>
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Image from '../../../models/management/Image';
import { Tournament } from '@/models/management/Tournament';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';

@Component
export default class AvailableTournamentsView2 extends Vue {
  tournaments: Tournament[] = [];
  search: string = '';
  sign: string = '';

  headers: object = [
    {
      text: 'Solved',
      value: 'solved',
      align: 'left',
      sortable: false,
      width: '5%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      sortable: false,
      width: '10%'
    },
    { text: 'Title', value: 'title', align: 'left' },
    { text: 'Starts', value: 'availableDate', align: 'left' },
    { text: 'Ends', value: 'conclusionDate', align: 'left' },
    { text: 'Questions', value: 'numberOfQuestions', align: 'left' },
    { text: 'Status', value: 'status', align: 'left' },
    { text: 'Assessment', value: 'assessmentDto.title', align: 'left' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getEnrolledTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async solveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    await this.$router.push({ name: 'solve-quiz' });
  }

  async showResults(tournament: Tournament) {
    let t = await RemoteServices.getTournament(
      tournament.id,
      this.$store.getters.getUser.username
    );
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.correctAnswers = t.solved.correctAnswers;
    statementManager.statementQuiz = t.solved.statementQuiz;
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
