<template>
  <div class="container">
    <h2>Own Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">Title</div>
        <div class="col">Starts</div>
        <div class="col">Ends</div>
        <div class="col">Questions</div>
        <div class="col">Status</div>
        <div class="col">Participants</div>
        <div class="col last-col"></div>
      </li>
      <li class="list-row" v-for="t in tournaments" :key="t.id">
        <div class="col" data-cy="title">
          {{ t.title }}
          <p v-show="false" data-cy="id">
            <span id="num"> {{ t.id }} </span>
          </p>
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
        <div class="col" v-if="t.status !== 'CANCELED' && t.status !== 'OPEN'">
          <v-btn
            class="btn"
            color="primary"
            @click="cancelTournament(t)"
            data-cy="cancel"
          >
            Cancel
          </v-btn>
        </div>
        <div class="col last-col" v-else></div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';

@Component
export default class OwnTournamentsView extends Vue {
  tournaments: Tournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getOwnTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
      margin: auto; /* Important */
      text-align: center;
      max-width: 15%;
      word-wrap: break-word;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
