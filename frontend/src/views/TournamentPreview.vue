<template>
  <v-card max-height="350" min-height="100" outlined hover>
    <v-row class="mx-0">
      <v-card-title class="pa-2">{{ tournament.title }}</v-card-title>
      <v-spacer />
      <div v-if="tournament.owner.role === 'TEACHER'" class="pa-2">
        <v-icon color="primary">fas fa-user-check</v-icon>
      </div>
    </v-row>
    <div class="mt-n4 text-left">
      <v-card-subtitle>Assessment:</v-card-subtitle>
      <v-card-text class="mt-n3">{{
        tournament.assessmentDto.title
      }}</v-card-text>
    </div>
    <v-row class="mx-0">
      <v-spacer />
      <div>
        <v-chip
          class="ma-1"
          small
          label
          :color="getStatusColor(tournament.status)"
          dark
          >{{ tournament.status }}</v-chip
        >
        <v-chip class="ma-1" small label color="grey" dark>{{
          tournament.numberOfQuestions + ' questions'
        }}</v-chip>
      </div>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/management/Tournament';

@Component
export default class TournamentPreview extends Vue {
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;
  getStatusColor(status: String) {
    switch (status) {
      case 'CREATED':
        return 'blue';
      case 'OPEN':
        return 'green';
      case 'CLOSED':
        return 'grey darken-2';
      case 'CANCELED':
        return 'red';
      default:
        return 'black';
    }
  }
}
</script>
<style scoped />
