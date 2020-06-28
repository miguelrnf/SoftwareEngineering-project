<template>
  <v-card flat>
    <div class="d-flex align-center mt-3">
      <v-card
        tile
        v-if="this.stats.mostPosts.length !== 0"
        class="pa-2 display-3 flex-grow-1"
        width="33%"
      >
        <v-chip label color="primary" class="mt-3">
          <v-icon color="white">fas fa-medal</v-icon>
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
        <v-chip label color="primary" class="mt-3">
          <v-icon color="white">fas fa-book</v-icon>
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
        <v-chip label color="primary" class="mt-3">
          <v-icon color="white">question_answer</v-icon>
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
        <v-chip label color="primary" class="mt-3">
          <v-icon color="white">assignment</v-icon>
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
        <v-chip label color="primary" class="mt-3">
          <v-icon color="white">fas fa-trophy</v-icon>
          <v-card-text class="headline"
            >{{ 'Most tournaments participated' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostTournamentsParticipatedStats"
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
        <v-chip label color="primary" class="mt-3">
          <v-icon color="white">fas fa-trophy</v-icon>
          <v-card-text class="headline"
            >{{ 'Most posts upvoted' }}
          </v-card-text>
        </v-chip>
        <GChart
          type="ColumnChart"
          :data="mostUpvotedPostsStats"
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
import User from '@/models/user/User';
import Post from '@/models/management/Post';
import { PostQuestion } from '@/models/management/PostQuestion';
import { PostAnswer } from '@/models/management/PostAnswer';
import Course from '@/models/user/Course';

interface CourseMap {
  [key: string]: Course[];
}

@Component({
  components: {
    GChart
  }
})
export default class DashboardLeaderboardsView extends Vue {
  stats: Leaderboards = {
    bestScores: [],
    mostApprovedSuggestions: [],
    mostPosts: [],
    mostQuizzesSolved: [],
    mostTournamentsParticipated: [],
    mostUpvotedPosts: []
  };
  bestScoreStats: Array<Object> = [];
  mostPostsStats: Array<Object> = [];
  mostApprovedSuggestionsStats: Array<Object> = [];
  mostQuizzesSolvedStats: Array<Object> = [];
  mostTournamentsParticipatedStats: Array<Object> = [];
  mostUpvotedPostsStats: Array<Object> = [];

  defaultUser: User = {
    courses: new (class implements CourseMap {
      [key: string]: Course[];
    })(),
    name: 'None',
    username: 'None',
    role: 'None',
    numberofsuggestions: 0,
    numberofsuggestionsapproved: 0,
    numberOfQuizzesSolved: 0,
    numberOfPostsSubmitted: 0,
    numberOfPTournamentsParticipated: 0,
    coursesNumber: 0,
    score: 0,
    currentTheme: 'None',
    dashboardPrivate: false,
    postsUpvoted: [],
    postsDownvoted: [],
    grade: 0
  };

  defaultPost: Post = {
    id: 0,
    upvotes: 0,
    key: 0,
    question: new PostQuestion(),
    answer: new PostAnswer(),
    comments: [],
    postStatus: false,
    discussStatus: false,
    postPrivacy: false,
    answerPrivacy: false,
    downvotes: 0,
    awards: []
  };

  chartOptions = {
    subtitle: '',
    backgroundColor: this.$vuetify.theme.currentTheme.background,
    tooltip: { textStyle: { fontSize: '14' } },
    legend: {
      position: 'none'
    },
    hAxis: {
      textStyle: {
        color: this.$vuetify.theme.currentTheme.font,
        fontSize: 14
      }
    }
  };

  async created() {
    console.log(this.$vuetify.theme.currentTheme.font);
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
      ['Name', 'Score', { role: 'style' }],
      [
        bestScores.length >= 1 ? bestScores[0].name : this.defaultUser.name,

        bestScores.length >= 1 ? bestScores[0].score : this.defaultUser.score,

        'red'
      ],
      [
        bestScores.length >= 2 ? bestScores[1].name : this.defaultUser.name,

        bestScores.length >= 2 ? bestScores[1].score : this.defaultUser.score,
        'blue'
      ],
      [
        bestScores.length >= 3 ? bestScores[2].name : this.defaultUser.name,

        bestScores.length >= 3 ? bestScores[2].score : this.defaultUser.score,
        'green'
      ],
      [
        bestScores.length >= 4 ? bestScores[3].name : this.defaultUser.name,

        bestScores.length >= 4 ? bestScores[3].score : this.defaultUser.score,
        'grey'
      ],
      [
        bestScores.length >= 5 ? bestScores[4].name : this.defaultUser.name,

        bestScores.length >= 5 ? bestScores[4].score : this.defaultUser.score,
        'orange'
      ]
    ];

    let mostPosts: { name: string; numberOfPostsSubmitted: number }[];
    this.stats?.mostPosts !== undefined
      ? (mostPosts = this.stats.mostPosts)
      : (mostPosts = []);
    this.shuffleArray(mostPosts);
    this.mostPostsStats = [
      ['Name', 'Posts Submitted', { role: 'style' }],
      [
        mostPosts.length >= 1 ? mostPosts[0].name : this.defaultUser.name,

        mostPosts.length >= 1
          ? mostPosts[0].numberOfPostsSubmitted
          : this.defaultUser.numberOfPostsSubmitted,

        'red'
      ],
      [
        mostPosts.length >= 2 ? mostPosts[1].name : this.defaultUser.name,

        mostPosts.length >= 2
          ? mostPosts[1].numberOfPostsSubmitted
          : this.defaultUser.numberOfPostsSubmitted,
        'blue'
      ],
      [
        mostPosts.length >= 3 ? mostPosts[2].name : this.defaultUser.name,

        mostPosts.length >= 3
          ? mostPosts[1].numberOfPostsSubmitted
          : this.defaultUser.numberOfPostsSubmitted,
        'green'
      ],
      [
        mostPosts.length >= 4 ? mostPosts[3].name : this.defaultUser.name,

        mostPosts.length >= 4
          ? mostPosts[3].numberOfPostsSubmitted
          : this.defaultUser.numberOfPostsSubmitted,
        'grey'
      ],
      [
        mostPosts.length >= 5 ? mostPosts[4].name : this.defaultUser.name,

        mostPosts.length >= 5
          ? mostPosts[4].numberOfPostsSubmitted
          : this.defaultUser.numberOfPostsSubmitted,
        'orange'
      ]
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
      ['Name', 'Approved Suggestions', { role: 'style' }],
      [
        mostApprovedSuggestions.length >= 1
          ? mostApprovedSuggestions[0].name
          : this.defaultUser.name,

        mostApprovedSuggestions.length >= 1
          ? mostApprovedSuggestions[0].numberofsuggestionsapproved == null
            ? 0
            : mostApprovedSuggestions[0].numberofsuggestionsapproved
          : this.defaultUser.numberofsuggestionsapproved,

        'red'
      ],
      [
        mostApprovedSuggestions.length >= 2
          ? mostApprovedSuggestions[1].name
          : this.defaultUser.name,

        mostApprovedSuggestions.length >= 2
          ? mostApprovedSuggestions[1].numberofsuggestionsapproved == null
            ? 0
            : mostApprovedSuggestions[1].numberofsuggestionsapproved
          : this.defaultUser.numberofsuggestionsapproved,
        'blue'
      ],
      [
        mostApprovedSuggestions.length >= 3
          ? mostApprovedSuggestions[2].name
          : this.defaultUser.name,

        mostApprovedSuggestions.length >= 3
          ? mostApprovedSuggestions[2].numberofsuggestionsapproved == null
            ? 0
            : mostApprovedSuggestions[2].numberofsuggestionsapproved
          : this.defaultUser.numberofsuggestionsapproved,
        'green'
      ],
      [
        mostApprovedSuggestions.length >= 4
          ? mostApprovedSuggestions[3].name
          : this.defaultUser.name,

        mostApprovedSuggestions.length >= 4
          ? mostApprovedSuggestions[3].numberofsuggestionsapproved == null
            ? 0
            : mostApprovedSuggestions[3].numberofsuggestionsapproved
          : this.defaultUser.numberofsuggestionsapproved,
        'grey'
      ],
      [
        mostApprovedSuggestions.length >= 5
          ? mostApprovedSuggestions[4].name
          : this.defaultUser.name,

        mostApprovedSuggestions.length >= 5
          ? mostApprovedSuggestions[4].numberofsuggestionsapproved == null
            ? 0
            : mostApprovedSuggestions[4].numberofsuggestionsapproved
          : this.defaultUser.numberofsuggestionsapproved,
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
      ['Name', 'Quizzes Solved', { role: 'style' }],
      [
        mostQuizzesSolved.length >= 1
          ? mostQuizzesSolved[0].name
          : this.defaultUser.name,

        mostQuizzesSolved.length >= 1
          ? mostQuizzesSolved[0].numberOfQuizzesSolved
          : this.defaultUser.numberOfQuizzesSolved,

        'red'
      ],
      [
        mostQuizzesSolved.length >= 2
          ? mostQuizzesSolved[1].name
          : this.defaultUser.name,

        mostQuizzesSolved.length >= 2
          ? mostQuizzesSolved[1].numberOfQuizzesSolved
          : this.defaultUser.numberOfQuizzesSolved,
        'blue'
      ],
      [
        mostQuizzesSolved.length >= 3
          ? mostQuizzesSolved[2].name
          : this.defaultUser.name,

        mostQuizzesSolved.length >= 3
          ? mostQuizzesSolved[2].numberOfQuizzesSolved
          : this.defaultUser.numberOfQuizzesSolved,
        'green'
      ],
      [
        mostQuizzesSolved.length >= 4
          ? mostQuizzesSolved[3].name
          : this.defaultUser.name,

        mostQuizzesSolved.length >= 4
          ? mostQuizzesSolved[3].numberOfQuizzesSolved
          : this.defaultUser.numberOfQuizzesSolved,
        'grey'
      ],
      [
        mostQuizzesSolved.length >= 5
          ? mostQuizzesSolved[4].name
          : this.defaultUser.name,

        mostQuizzesSolved.length >= 5
          ? mostQuizzesSolved[4].numberOfQuizzesSolved
          : this.defaultUser.numberOfQuizzesSolved,
        'orange'
      ]
    ];

    let mostTournamentsParticipated: {
      name: string;
      numberOfPTournamentsParticipated: number;
    }[];
    this.stats?.mostTournamentsParticipated !== undefined
      ? (mostTournamentsParticipated = this.stats.mostTournamentsParticipated)
      : (mostTournamentsParticipated = []);
    this.shuffleArray(mostTournamentsParticipated);
    this.mostTournamentsParticipatedStats = [
      ['Name', 'Tournaments Participated', { role: 'style' }],
      [
        mostTournamentsParticipated.length >= 1
          ? mostTournamentsParticipated[0].name
          : this.defaultUser.name,

        mostTournamentsParticipated.length >= 1
          ? mostTournamentsParticipated[0].numberOfPTournamentsParticipated
          : this.defaultUser.numberOfPTournamentsParticipated,

        'red'
      ],
      [
        mostTournamentsParticipated.length >= 2
          ? mostTournamentsParticipated[1].name
          : this.defaultUser.name,

        mostTournamentsParticipated.length >= 2
          ? mostTournamentsParticipated[1].numberOfPTournamentsParticipated
          : this.defaultUser.numberOfPTournamentsParticipated,

        'blue'
      ],
      [
        mostTournamentsParticipated.length >= 3
          ? mostTournamentsParticipated[2].name
          : this.defaultUser.name,

        mostTournamentsParticipated.length >= 3
          ? mostTournamentsParticipated[2].numberOfPTournamentsParticipated
          : this.defaultUser.numberOfPTournamentsParticipated,

        'green'
      ],
      [
        mostTournamentsParticipated.length >= 4
          ? mostTournamentsParticipated[3].name
          : this.defaultUser.name,

        mostTournamentsParticipated.length >= 4
          ? mostTournamentsParticipated[3].numberOfPTournamentsParticipated
          : this.defaultUser.numberOfPTournamentsParticipated,

        'grey'
      ],
      [
        mostTournamentsParticipated.length >= 5
          ? mostTournamentsParticipated[4].name
          : this.defaultUser.name,

        mostTournamentsParticipated.length >= 5
          ? mostTournamentsParticipated[4].numberOfPTournamentsParticipated
          : this.defaultUser.numberOfPTournamentsParticipated,
        'orange'
      ]
    ];

    let mostUpvotedPosts: {
      id: number;
      upvotes: number;
    }[];
    this.stats?.mostUpvotedPosts !== undefined
      ? (mostUpvotedPosts = this.stats.mostUpvotedPosts)
      : (mostUpvotedPosts = []);
    this.shuffleArray(mostUpvotedPosts);
    this.mostUpvotedPostsStats = [
      ['PostId', 'Number Of Upvotes', { role: 'style' }],
      [
        mostUpvotedPosts.length >= 1
          ? mostUpvotedPosts[0].id
          : this.defaultPost.id,

        mostUpvotedPosts.length >= 1
          ? mostUpvotedPosts[0].upvotes
          : this.defaultPost.upvotes,

        'red'
      ],
      [
        mostUpvotedPosts.length >= 2
          ? mostUpvotedPosts[1].id
          : this.defaultPost.id - 1,

        mostUpvotedPosts.length >= 2
          ? mostUpvotedPosts[1].upvotes
          : this.defaultPost.upvotes,

        'blue'
      ],
      [
        mostUpvotedPosts.length >= 3
          ? mostUpvotedPosts[2].id
          : this.defaultPost.id - 2,

        mostUpvotedPosts.length >= 3
          ? mostUpvotedPosts[2].upvotes
          : this.defaultPost.upvotes,

        'green'
      ],
      [
        mostUpvotedPosts.length >= 4
          ? mostUpvotedPosts[3].id
          : this.defaultPost.id - 3,

        mostUpvotedPosts.length >= 4
          ? mostUpvotedPosts[3].upvotes
          : this.defaultPost.upvotes,

        'grey'
      ],
      [
        mostUpvotedPosts.length >= 5
          ? mostUpvotedPosts[4].id
          : this.defaultPost.id - 4,

        mostUpvotedPosts.length >= 5
          ? mostUpvotedPosts[4].upvotes
          : this.defaultPost.upvotes,

        'orange'
      ]
    ];
  }
}
</script>

<style lang="scss" scoped />
