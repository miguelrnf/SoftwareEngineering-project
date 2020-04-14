<template>
  <div class="container">
    <h2>Available Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Starts</div>
        <div class="col">Ends</div>
        <div class="col">Questions</div>
        <div class="col last-col"></div>
      </li>
      <li class="list-row" v-for="t in tournaments" :key="t.id">
        <div class="col">
          {{ t.title }}
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
        <div>
          <v-btn class="btn" color="primary" @click.stop="isEnrolled(t)">
            Details
          </v-btn>
        </div>
      </li>
      <v-dialog
        :retain-focus="false"
        v-model="dialog"
        class="container"
        max-width="70%"
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
            <v-btn color="primary" text @click="signButton(currentTournament)">
              {{ sign }}
            </v-btn>
            <v-btn color="primary" text @click="dialog = false">
              Close
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';
import { Student } from '@/models/management/Student';

@Component
export default class AvailableTournamentsView extends Vue {
  dialog: boolean = false;
  sign: string = '';
  tournaments: Tournament[] = [];
  currentTournament: Tournament = new Tournament();

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getOpenedTournaments();
      this.currentTournament = this.tournaments[0];
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async isEnrolled(t: Tournament) {
    this.dialog = true;
    this.currentTournament = t;
    let s: Student;
    if (t.enrolledStudents.length == 0) {
      this.sign = 'Sign In';
      return;
    }
    for (s of t.enrolledStudents) {
      if (s.username == this.$store.getters.getUser.username) {
        this.sign = 'Sign Out';
      } else {
        this.sign = 'Sign In';
      }
    }
  }

  async signButton(t: Tournament) {
    if (this.sign == 'Sign In') {
      await this.enrollTournament(t.id);
      this.sign = 'Sign Out';
    } else {
      await this.unenrollTournament(t.id);
      this.sign = 'Sign In';
    }
    await this.$router.push({ name: 'enrolled-Tournaments' });
  }

  async enrollTournament(id: Number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.enrollTournament(id);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async unenrollTournament(id: Number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.unenrollTournament(id);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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

  h3 {
    color: blue;
  }

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
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }

    .btn {
      flex-basis: 20% !important;
      display: flex;
    }
  }
}
</style>
