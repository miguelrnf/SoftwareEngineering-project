<template>
  <div class="container">
    <h2>All Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="colResult"></div>
        <div class="col">Title</div>
        <div class="col">Status</div>
        <div class="col">Participants</div>
        <div class="col">Owner</div>
        <div class="col last-col">
          <v-btn
            class="primary--text"
            @click="createTournament()"
            data-cy="create"
          >
            Create
          </v-btn>
        </div>
      </li>
      <li class="list-row" v-for="to in tournaments" :key="to.id">
        <div class="colResult" data-cy="results">
          <v-icon
            large
            class="mr-2"
            @click="showQuizAnswers(to)"
            v-if="showResults(to)"
            >fas fa-poll</v-icon
          >
        </div>
        <div class="col" data-cy="title">
          {{ to.title }}
          <p v-show="false" data-cy="id">
            <span id="num"> {{ to.id }} </span>
          </p>
        </div>
        <div class="col">
          {{ to.status }}
        </div>
        <div class="col">
          {{ to.enrolledStudents.length }}
        </div>
        <div class="col">
          {{ to.owner.name }}
        </div>
        <div class="col" v-if="showCancel(to)">
          <v-btn
            class="btn"
            color="primary"
            @click="cancelTournament(to)"
            data-cy="cancel"
          >
            Cancel
          </v-btn>
        </div>
        <div class="col last-col" v-else>
          <v-btn
            class="btn"
            color="primary"
            @click.stop="openDialog(to)"
            data-cy="details"
          >
            Details
          </v-btn>
        </div>
      </li>
      <v-dialog
        :retain-focus="false"
        v-model="dialog"
        class="container"
        max-width="70%"
        v-if="iscreated"
      >
        <v-card>
          <v-card-title class="justify-center">
            <v-card-actions>
              <h3>{{ currentTournament.title }}</h3>
            </v-card-actions>
          </v-card-title>
          <v-card-text>
            <v-card-actions>
              <div class="container">
                <ul>
                  <li class="cth">
                    <div class="col">Starts</div>
                    <div class="col">Ends</div>
                    <div class="col">Assessment</div>
                    <div class="col">Questions</div>
                    <div class="col">Participants</div>
                  </li>
                  <li class="lt">
                    <div class="col">
                      {{ currentTournament.availableDate }}
                    </div>
                    <div class="col">
                      {{ currentTournament.conclusionDate }}
                    </div>
                    <div class="col">
                      {{ currentTournament.assessmentDto.title }}
                    </div>
                    <div class="col">
                      {{ currentTournament.numberOfQuestions }}
                    </div>
                    <div class="col">
                      {{ currentTournament.enrolledStudents.length }}
                    </div>
                  </li>
                </ul>
              </div>
            </v-card-actions>
          </v-card-text>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="dialog = false">
              Close
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </ul>
    <show-quiz-answers-dialog
      v-if="quizAnswers"
      v-model="quizAnswersDialog"
      :quiz-answers="quizAnswers"
      :correct-sequence="correctSequence"
      :timeToSubmission="timeToSubmission"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import { QuizAnswer } from '@/models/management/QuizAnswer';
import ShowQuizAnswersDialog from '@/views/teacher/quizzes/ShowQuizAnswersDialog.vue';

@Component({
  components: {
    'show-quiz-answers-dialog': ShowQuizAnswersDialog
  }
})
export default class AllTeacherTournaments extends Vue {
  tournaments: Tournament[] = [];
  dialog: boolean = false;
  currentTournament: Tournament = new Tournament();
  iscreated: boolean = false;
  quizAnswers: QuizAnswer[] = [];
  correctSequence: number[] = [];
  quizAnswersDialog: boolean = false;
  timeToSubmission: number = 0;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getAllTournaments();
      if (this.tournaments.length != 0) {
        this.iscreated = true;
        this.currentTournament = this.tournaments[0];
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showQuizAnswers(t: Tournament) {
    try {
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

  openDialog(t: Tournament) {
    this.dialog = true;
    this.currentTournament = t;
  }

  setTournamentStatus(newT: Tournament, t: Tournament) {
    t.status = newT.status;
  }

  showCancel(to: Tournament) {
    return (
      to.status !== 'CANCELED' &&
      to.status !== 'OPEN' &&
      to.owner.username === this.$store.getters.getUser.username
    );
  }

  showResults(t: Tournament) {
    console.log(t);
    console.log(t.quiz.id);
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

    .cth {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      color: Black;
      font-size: 14px;
      font-weight: bold;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .lt {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }

    .col {
      margin: auto; /* Important */
      text-align: center;
      max-width: 25%;
      word-wrap: break-word;
    }

    .colResult {
      flex-basis: 5% !important;
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
