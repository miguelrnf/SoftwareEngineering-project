<template>
  <v-card class="table">
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
          <v-spacer />
          <v-btn class="primary" @click="createTournament()" data-cy="create">
            Create
          </v-btn>
        </v-card-title>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="details"
              class="mr-2"
              v-on="on"
              @click="openDialog(item)"
              >fas fa-info-circle</v-icon
            >
          </template>
          <span>Details</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              v-if="showButtons(item)"
              data-cy="edit"
              class="mr-2"
              v-on="on"
              @click="openEditDialog(item)"
              >edit</v-icon
            >
          </template>
          <span>Edit</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              v-if="showButtons(item)"
              class="btn"
              color="red"
              v-on="on"
              @click="cancelTournament(item)"
              data-cy="cancel"
              >fas fa-ban</v-icon
            >
          </template>
          <span>Cancel the tournament</span>
        </v-tooltip>
        <v-tooltip bottom v-if="showResults(item)">
          <template v-slot:activator="{ on }">
            <v-icon large class="mr-2" v-on="on" @click="showQuizAnswers(item)"
              >fas fa-poll</v-icon
            >
          </template>
          <span>View Results</span>
        </v-tooltip>
      </template>

      <template v-slot:item.title="{ item }">
        <p v-html="convertMarkDown(item.title, null)" />
        <p v-show="false" data-cy="id">
          <span id="num"> {{ item.id }} </span>
        </p>
      </template>
      <template v-slot:item.status="{ item }">
        <p v-html="convertMarkDown(item.status, null)" />
      </template>
      <template v-slot:item.enrolledStudents.length="{ item }">
        <p>{{ item.enrolledStudents.length }}</p>
      </template>
      <template v-slot:item.owner.name="{ item }">
        <p v-html="convertMarkDown(item.owner.name, null)" />
      </template>
      <template v-slot:item.type="{ item }">
        <p v-html="convertMarkDown(item.type, null)" />
      </template>
    </v-data-table>
    <show-quiz-answers-dialog
      v-if="quizAnswers"
      v-model="quizAnswersDialog"
      :quiz-answers="quizAnswers"
      :correct-sequence="correctSequence"
      :timeToSubmission="timeToSubmission"
    />
    <show-tournament-dialog
      v-if="currentTournament"
      :dialog="tournamentDialog"
      :tournament="currentTournament"
      :dashboard="false"
      v-on:close-show-tournament-dialog="tournamentDialog = false"
    />
    <show-edit-tournament-dialog
      v-if="currentTournament"
      v-model="editDialog"
      :tournament="currentTournament"
      v-on:save-tournament="onSaveTournamentEvent"
      v-on:close-show-edit-tournament-dialog="editDialog = false"
    />
  </v-card>
</template>
<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Image from '../../models/management/Image';
import { Tournament } from '@/models/management/Tournament';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ShowQuizAnswersDialog from '@/views/teacher/quizzes/ShowQuizAnswersDialog.vue';
import RemoteServices from '@/services/RemoteServices';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import TournamentViewDialog from '@/views/TournamentViewDialog.vue';
import CreateTournamentsViewDialog from '@/views/teacher/EditTournamentsViewDialog.vue';

@Component({
  components: {
    'show-tournament-dialog': TournamentViewDialog,
    'show-edit-tournament-dialog': CreateTournamentsViewDialog,
    'show-quiz-answers-dialog': ShowQuizAnswersDialog
  }
})
export default class AllTeacherTournaments extends Vue {
  tournaments: Tournament[] = [];
  tournamentDialog: boolean = false;
  editDialog: boolean = false;
  search: string = '';
  currentTournament: Tournament | null = null;
  iscreated: boolean = false;
  quizAnswers: QuizAnswer[] = [];
  correctSequence: number[] = [];
  quizAnswersDialog: boolean = false;
  timeToSubmission: number = 0;

  headers: object = [
    { text: 'Actions', value: 'action', align: 'center', sortable: false },
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Status', value: 'status', align: 'center' },
    { text: 'Participants', value: 'enrolledStudents.length', align: 'center' },
    { text: 'Owner', value: 'owner.name', align: 'center' },
    { text: 'Type', value: 'type', align: 'center' }
  ];

  @Watch('editDialog')
  closeError() {
    if (!this.editDialog) {
      this.currentTournament = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getAllTournaments();
      if (this.tournaments.length != 0) {
        this.iscreated = true;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showQuizAnswers(t: Tournament) {
    try {
      console.log(t);
      let quizAnswers: QuizAnswers = await RemoteServices.getQuizAnswers(
        t.quiz.id
      );

      this.quizAnswers = quizAnswers.quizAnswers;
      this.correctSequence = quizAnswers.correctSequence;
      this.timeToSubmission = quizAnswers.timeToSubmission;
      this.quizAnswersDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async createTournament() {
    try {
      await this.$router.push({ name: 'create-teacher-tournament' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  openDialog(t: Tournament) {
    this.tournamentDialog = true;
    this.currentTournament = t;
  }

  openEditDialog(t: Tournament) {
    this.editDialog = true;
    this.currentTournament = t;
  }

  onSaveTournamentEvent(tour: Tournament) {
    this.tournaments = this.tournaments.filter(t => t.id !== tour.id);
    this.tournaments.unshift(tour);
    this.editDialog = false;
    this.currentTournament = null;
  }

  setTournamentStatus(newT: Tournament, t: Tournament) {
    t.status = newT.status;
  }

  showButtons(t: Tournament) {
    if (this.$store.getters.getUser != null) {
      return (
        t.enrolledStudents.length == 0 &&
        t.status === 'CREATED' &&
        t.owner.username === this.$store.getters.getUser.username
      );
    }
  }

  showResults(t: Tournament) {
    return t.status === 'CLOSED' && t.quiz.id;
  }

  async cancelTournament(t: Tournament) {
    let newT;
    if (confirm('Are you sure you want to cancel this tournament?')) {
      try {
        newT = await RemoteServices.cancelTournament(t.id);
        await this.setTournamentStatus(newT, t);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }
}
</script>
