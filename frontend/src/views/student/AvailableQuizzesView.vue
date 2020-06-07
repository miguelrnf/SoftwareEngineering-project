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
            <v-icon large class="mr-2" v-on="on" @click="solveQuiz(item)"
              >fas fa-chevron-circle-right</v-icon
            >
          </template>
          <span>Solve Quiz</span>
        </v-tooltip>
      </template>

      <template v-slot:item.title="{ item }">
        <p @click="solveQuiz(item)" style="cursor: pointer">
          {{ item.title }}
        </p>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';

@Component
export default class AvailableQuizzesView extends Vue {
  quizzes: StatementQuiz[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Actions',
      value: 'action',
      align: 'left',
      width: '50px',
      sortable: false
    },
    { text: 'Title', value: 'title', align: 'left' },
    {
      text: 'Available since',
      value: 'availableDate',
      align: 'left'
    },
    {
      text: 'Available until',
      value: 'conclusionDate',
      align: 'left'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.quizzes = (await RemoteServices.getAvailableQuizzes()).reverse();
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
