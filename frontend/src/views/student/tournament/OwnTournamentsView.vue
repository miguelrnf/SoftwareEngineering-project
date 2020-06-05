<template>
  <v-card class="table">
    <v-card-title style="font-size: xx-large"
      >Own Tournaments</v-card-title
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

      <template v-slot:item.action="{ item }">
        <v-btn
          v-if="item.status === 'CREATED'"
          class="mr-2"
          color="primary"
          @click="cancelTournament(item)"
          >Cancel
        </v-btn>
      </template>
      <template v-slot:item.title="{ item }">
        <p v-html="convertMarkDown(item.title, null)" />
        <p v-show="false" data-cy="id">
          <span id="num"> {{ item.id }} </span>
        </p>
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
      <template v-slot:item.enrolledStudents.length="{ item }">
        <p>{{ item.enrolledStudents.length }}</p>
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

@Component
export default class AvailableTournamentsView2 extends Vue {
  tournaments: Tournament[] = [];
  search: string = '';
  sign: string = '';

  headers: object = [
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
    { text: 'Participants', value: 'enrolledStudents.length', align: 'left' }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getOwnTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async setTournamentStatus(newT: Tournament, t: Tournament) {
    t.status = newT.status;
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
