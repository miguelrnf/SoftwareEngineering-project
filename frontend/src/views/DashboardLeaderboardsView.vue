<template>
  <v-card flat>
    <div class="d-flex align-center mt-3">
      <v-card
        tile
        v-if="this.stats.mostPosts.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"
      >
        <v-chip label color="white" class="mt-3">
          <v-icon color="grey darken-1">fas fa-medal</v-icon>
          <v-card-text class="headline"
            >{{ 'Best player scores' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="bestScoreStats"
          :options="chartOptions"
        />
      </v-card>
      <v-card v-else class="pb-10 " height="100%">
        <v-card-text class="pt-12">NO PLAYER SCORES</v-card-text>
        <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
      </v-card>
      <v-card
        tile
        v-if="this.stats.mostPosts.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"
      >
        <v-chip label color="white" class="mt-3">
          <v-icon color="grey darken-1">fas fa-book</v-icon>
          <v-card-text class="headline"
            >{{ 'Most posts submitted' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostPostsStats"
          :options="chartOptions"
        />
      </v-card>
      <v-card v-else class="pb-10 " height="100%">
        <v-card-text class="pt-12">NO POSTS SUBMITTED</v-card-text>
        <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
      </v-card>
      <v-card
        tile
        v-if="this.stats.mostApprovedSuggestions.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"
      >
        <v-chip label color="white" class="mt-3">
          <v-icon color="grey darken-1">question_answer</v-icon>
          <v-card-text class="headline"
            >{{ 'Most suggestions approved' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostApprovedSuggestionsStats"
          :options="chartOptions"
        />
      </v-card>
      <v-card v-else class="pb-10 " height="100%">
        <v-card-text class="pt-12">NO SUGGESTIONS SUBMITTED</v-card-text>
        <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
      </v-card>
    </div>
    <div class="d-flex align-center mt-3">
      <v-card
        v-if="this.stats.mostApprovedSuggestions.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"
      >
        <v-chip label color="white" class="mt-3">
          <v-icon color="grey darken-1">assignment</v-icon>
          <v-card-text class="headline"
            >{{ 'Most quizzes solved' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostQuizzesSolvedStats"
          :options="chartOptions"
        />
      </v-card>
      <v-card v-else class="pb-10 " height="100%">
        <v-card-text class="pt-12">NO QUIZZES SOLVED</v-card-text>
        <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
      </v-card>
      <v-card
        v-if="this.stats.mostApprovedSuggestions.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"
      >
        <v-chip label color="white" class="mt-3">
          <v-icon color="grey darken-1">fas fa-trophy</v-icon>
          <v-card-text class="headline"
            >{{ 'Most tournaments participated' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostTournamentsParticipated"
          :options="chartOptions"
        />
      </v-card>
      <v-card v-else class="pb-10 " height="100%">
        <v-card-text class="pt-12">NO TOURNAMENTS PARTICIPATED</v-card-text>
        <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
      </v-card>
      <v-card
        v-if="this.stats.mostApprovedSuggestions.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"

      >
        <v-chip label color="white" class="mt-3">
          <v-icon color="grey darken-1">fas fa-trophy</v-icon>
          <v-card-text class="headline"
            >{{ 'Most posts upvoted' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostTournamentsParticipated"
          :options="chartOptions"
        />
      </v-card>
      <v-card v-else class="pb-10 " height="100%">
        <v-card-text class="pt-12">NO TOURNAMENTS PARTICIPATED</v-card-text>
        <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
      </v-card>
    </div>
  </v-card>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import { GChart } from 'vue-google-charts';
  import { Leaderboards } from '@/models/management/Leaderboards';

  @Component({
  components: {
    GChart
  }
})
export default class DashboardLeaderboardsView extends Vue {
  stats: Leaderboards | null = null;
  bestScoreStats: Array<Object> = [];
  mostPostsStats: Array<Object> = [];
  mostApprovedSuggestionsStats: Array<Object> = [];
  mostQuizzesSolvedStats: Array<Object> = [];
  mostTournamentsParticipated: Array<Object> = [];

  chartOptions = {
    legend: { position: 'none' },

    subtitle: '',
    backgroundColor: this.$vuetify.theme.currentTheme.background,


  };

  async created() {
    this.stats = await RemoteServices.getLeaderboards();
    this.getStats();
  }

  shuffleArray(array: Array<Object>) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
  }

  getStats() {
    let bestScores: { name: string; score: number }[];
    this.stats?.bestScores !== undefined
      ? (bestScores = this.stats.bestScores)
      : (bestScores = []);
    this.shuffleArray(bestScores);
    this.bestScoreStats = [
      ['Name', 'Score',{ role: "style" }],
      [bestScores[0].name, bestScores[0].score, 'red'],
      [bestScores[1].name, bestScores[1].score, 'blue'],
      [bestScores[2].name, bestScores[2].score, 'green'],
      [bestScores[3].name, bestScores[3].score, 'grey'],
      [bestScores[4].name, bestScores[4].score, 'orange']
    ];

    let mostPosts: { name: string; numberOfPostsSubmitted: number }[];
    this.stats?.mostPosts !== undefined
      ? (mostPosts = this.stats.mostPosts)
      : (mostPosts = []);
    this.shuffleArray(mostPosts);
    this.mostPostsStats = [
      ['Name', 'Posts Submitted',{ role: "style" }],
      [mostPosts[0].name, mostPosts[0].numberOfPostsSubmitted , 'red'],
      [mostPosts[1].name, mostPosts[1].numberOfPostsSubmitted, 'blue'],
      [mostPosts[2].name, mostPosts[2].numberOfPostsSubmitted, 'green'],
      [mostPosts[3].name, mostPosts[3].numberOfPostsSubmitted, 'grey'],
      [mostPosts[4].name, mostPosts[4].numberOfPostsSubmitted, 'orange']
    ];

    let mostApprovedSuggestions: {
      name: string;
      numberofsuggestionsapproved: number;
    }[];
    this.stats?.mostApprovedSuggestions !== undefined
      ? (mostApprovedSuggestions = this.stats.mostApprovedSuggestions)
      : (mostApprovedSuggestions = []);
    this.shuffleArray(mostApprovedSuggestions);
    this.mostApprovedSuggestionsStats = [
      ['Name', 'Approved Suggestions',{ role: "style" }],
      [
        mostApprovedSuggestions[0].name,
        mostApprovedSuggestions[0].numberofsuggestionsapproved == null
          ? 0
          : mostApprovedSuggestions[0].numberofsuggestionsapproved,
        'red'
      ],
      [
        mostApprovedSuggestions[1].name,
        mostApprovedSuggestions[1].numberofsuggestionsapproved == null
          ? 0
          : mostApprovedSuggestions[1].numberofsuggestionsapproved,
        'blue'
      ],
      [
        mostApprovedSuggestions[2].name,
        mostApprovedSuggestions[2].numberofsuggestionsapproved == null
          ? 0
          : mostApprovedSuggestions[2].numberofsuggestionsapproved,
        'green'
      ],
      [
        mostApprovedSuggestions[3].name,
        mostApprovedSuggestions[3].numberofsuggestionsapproved == null
          ? 0
          : mostApprovedSuggestions[3].numberofsuggestionsapproved,
        'grey'
      ],
      [
        mostApprovedSuggestions[4].name,
        mostApprovedSuggestions[4].numberofsuggestionsapproved == null
          ? 0
          : mostApprovedSuggestions[4].numberofsuggestionsapproved,
        'orange'
      ]
    ];

    let mostQuizzesSolved: {
      name: string;
      numberOfQuizzesSolved: number;
    }[];
    this.stats?.mostQuizzesSolved !== undefined
      ? (mostQuizzesSolved = this.stats.mostQuizzesSolved)
      : (mostQuizzesSolved = []);
    this.shuffleArray(mostQuizzesSolved);
    this.mostQuizzesSolvedStats = [
      ['Name', 'Quizzes Solved',{ role: "style" }],
      [mostQuizzesSolved[0].name, mostQuizzesSolved[0].numberOfQuizzesSolved , 'red'],
      [mostQuizzesSolved[1].name, mostQuizzesSolved[1].numberOfQuizzesSolved , 'blue'],
      [mostQuizzesSolved[2].name, mostQuizzesSolved[2].numberOfQuizzesSolved , 'green'],
      [mostQuizzesSolved[3].name, mostQuizzesSolved[3].numberOfQuizzesSolved , 'grey'],
      [mostQuizzesSolved[4].name, mostQuizzesSolved[4].numberOfQuizzesSolved , 'orange']
    ];

    let mostTournamentsParticipated: {
      name: string;
      numberOfPTournamentsParticipated: number;
    }[];
    this.stats?.mostTournamentsParticipated !== undefined
      ? (mostTournamentsParticipated = this.stats.mostTournamentsParticipated)
      : (mostTournamentsParticipated = []);
    this.shuffleArray(mostTournamentsParticipated);
    this.mostTournamentsParticipated = [
      ['Name', 'Tournaments Participated',{ role: "style" }],
      [
        mostTournamentsParticipated[0].name,
        mostTournamentsParticipated[0].numberOfPTournamentsParticipated,
        'red'
      ],
      [
        mostTournamentsParticipated[1].name,
        mostTournamentsParticipated[1].numberOfPTournamentsParticipated,
        'blue'
      ],
      [
        mostTournamentsParticipated[2].name,
        mostTournamentsParticipated[2].numberOfPTournamentsParticipated,
        'green'
      ],
      [
        mostTournamentsParticipated[3].name,
        mostTournamentsParticipated[3].numberOfPTournamentsParticipated,
        'grey'
      ],
      [
        mostTournamentsParticipated[4].name,
        mostTournamentsParticipated[4].numberOfPTournamentsParticipated,
        'orange'
      ]
    ];
  }
}
</script>

<style lang="scss" scoped></style>
