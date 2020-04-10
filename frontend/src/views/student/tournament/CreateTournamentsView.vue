<template>
  <div class="container">
    <h2>Create Tournament</h2>
    <v-container class="create-buttons">
      <v-card-text>
        <v-text-field
          v-model="tournamentManager.title"
          label="*Title"
          data-cy="title"
        />
      </v-card-text>
      <v-container>
        <p>Assessment</p>
        <v-btn-toggle
          v-model="tournamentManager.assessmentId"
          mandatory
          class="button-group"
        >
          <v-btn
            v-for="assessment in availableAssessments"
            text
            :value="assessment.id"
            :key="assessment.id"
            data-cy="AssessmentTitle"
            >{{ assessment.title }}</v-btn
          >
          <!--          <v-btn text value="all">All</v-btn>-->
        </v-btn-toggle>
      </v-container>

      <!--      <v-container>
        <p class="pl-0">Questions</p>
        <v-btn-toggle
          v-model="statementManager.questionType"
          mandatory
          class="button-group"
        >
          <v-btn text value="failed">Only Failed</v-btn>
          <v-btn text value="new">Only New</v-btn>
          <v-btn text value="all">All</v-btn>
        </v-btn-toggle>
      </v-container>-->

      <v-container>
        <p class="pl-0">Number of Questions</p>
        <v-btn-toggle
          v-model="tournamentManager.numberOfQuestions"
          mandatory
          class="button-group"
        >
          <v-btn text value="10">10</v-btn>
          <v-btn text value="20">20</v-btn>
          <v-btn text value="30">30</v-btn>
          <v-btn text value="40">40</v-btn>
        </v-btn-toggle>
      </v-container>
      <v-card-text>
        <v-row>
          <v-col cols="12" sm="6">
            <v-datetime-picker
              label="*Available Date"
              format="yyyy-MM-dd HH:mm"
              data-cy="availableDate"
              v-model="tournamentManager.availableDate"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
            >
            </v-datetime-picker>
          </v-col>
          <v-spacer></v-spacer>
          <v-col cols="12" sm="6">
            <v-datetime-picker
              label="*Conclusion Date"
              format="yyyy-MM-dd HH:mm"
              v-model="tournamentManager.conclusionDate"
              date-format="yyyy-MM-dd"
              time-format="HH:mm"
              data-cy="conclusionDate"
            >
            </v-datetime-picker>
          </v-col>
        </v-row>
      </v-card-text>
      <v-container>
        <v-btn
          @click="createTournament"
          depressed
          color="primary"
          data-cy="createButton"
        >
          Create tournament
        </v-btn>
      </v-container>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';
import TournamentManager from '@/models/tournament/TournamentManager';

@Component
export default class CreateTournamentView extends Vue {
  tournamentManager: TournamentManager = TournamentManager.getInstance;
  availableAssessments: Assessment[] = [];

  async created() {
    await this.$store.dispatch('loading');
    this.tournamentManager.reset();
    try {
      this.availableAssessments = await RemoteServices.getAvailableAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async createTournament() {
    try {
      await this.tournamentManager.getNewTournament();
      await this.$router.push({ name: 'own-Tournaments' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped>
.create-buttons {
  width: 80% !important;
  background-color: white;
  border-width: 10px;
  border-style: solid;
  border-color: #818181;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>
