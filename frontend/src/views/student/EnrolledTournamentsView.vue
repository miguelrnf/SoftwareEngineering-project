<template>
  <div class="container">
    <h2>Enrolled Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Starts</div>
        <div class="col">Ends</div>
        <div class="col">Questions</div>
        <div class="col">Status</div>
        <div class="col">Participants</div>
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
        <div class="col">
          {{ t.status }}
        </div>
        <div class="col">
          {{ t.enrolledStudents.length }}
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';

@Component
export default class EnrolledTournamentsView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getEnrolledTournaments()).reverse();
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
  }
}
</style>
