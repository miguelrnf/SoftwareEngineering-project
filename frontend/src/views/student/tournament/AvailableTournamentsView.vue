<template>
  <v-card class="table">
    <v-card-title style="font-size: xx-large"
      >Available Tournaments</v-card-title
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

      <template v-slot:item.sign="{ item }">
        <v-tooltip v-if="isEnrolled(item)" bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" color="success"
              >far fa-dot-circle</v-icon
            >
          </template>
          <span>Enrolled</span>
        </v-tooltip>
        <v-tooltip v-else bottom>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" color="error"
              >fas fa-dot-circle</v-icon
            >
          </template>
          <span>Not enrolled</span>
        </v-tooltip>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="details"
              class="mr-2"
              v-on="on"
              @click="enrollUpdate(item)"
              >fas fa-info-circle</v-icon
            >
          </template>
          <span>Details</span>
        </v-tooltip>
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
    </v-data-table>
    <v-dialog
      :retain-focus="false"
      v-model="tournamentDialog"
      class="container"
      max-width="70%"
      v-if="iscreated"
    >
      <v-card>
        <show-tournament-dialog
          v-if="currentTournament"
          :dialog="tournamentDialog"
          :tournament="currentTournament"
          :dashboard="false"
          :sign="sign"
          v-on:close-show-tournament-dialog="tournamentDialog = false"
          v-on:sign-in="signButton(currentTournament)"
        />
      </v-card>
    </v-dialog>
  </v-card>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Image from '../../../models/management/Image';
import { Tournament } from '@/models/management/Tournament';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import { Student } from '@/models/management/Student';
import TournamentViewDialog from '@/views/TournamentViewDialog.vue';

@Component({
  components: {
    'show-tournament-dialog': TournamentViewDialog
  }
})
export default class AvailableTournamentsView2 extends Vue {
  tournaments: Tournament[] = [];
  search: string = '';
  currentTournament: Tournament = new Tournament();
  iscreated: boolean = false;
  tournamentDialog: boolean = false;
  sign: string = '';

  headers: object = [
    { text: '', value: 'sign', align: 'left', sortable: false, width: '5%' },
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
    { text: 'Questions', value: 'numberOfQuestions', align: 'left' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getOpenedTournaments();
      if (this.tournaments.length != 0) {
        this.iscreated = true;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  enrollUpdate(t: Tournament) {
    this.tournamentDialog = true;
    this.currentTournament = t;

    if (this.isEnrolled(t)) {
      this.sign = 'Sign Out';
    } else {
      this.sign = 'Sign In';
    }
     if (this.sign === 'Sign In' && t.type === 'ADVANCED') {
      this.sign = t.cost + ' Achandos';
    }
  } 

  isEnrolled(t: Tournament): Boolean {
    let s: Student;
    if (t.enrolledStudents.length == 0) {
      return false;
    }
    for (s of t.enrolledStudents) {
      if (s.username == this.$store.getters.getUser.username) return true;
    }
    return false;
  }

  async signButton(t: Tournament) {
    if (this.sign == 'Sign Out') {
      await this.unenrollTournament(t.id);
      this.sign = 'Sign In';
    } else {
      await this.enrollTournament(t.id);
      this.sign = 'Sign Out';
    }
  }

  async enrollTournament(id: Number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.enrollTournament(id);
      await this.$store.dispatch('updateScore');
      await this.$router.push({ name: 'enrolled-Tournaments' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async unenrollTournament(id: Number) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.unenrollTournament(id);
      await this.$store.dispatch('updateScore');
      await this.$router.push({ name: 'enrolled-Tournaments' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
