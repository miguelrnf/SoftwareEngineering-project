<template>
  <v-card flat>
    <v-row class="d-inline-flex">
      <v-col>
        <v-card
          v-if="
            (this.stats.approveSuggestions !== null
              ? this.stats.approveSuggestions
              : 0) +
              (this.stats.rejectedSuggestions !== null
                ? this.stats.rejectedSuggestions
                : 0) +
              (this.stats.pendingSuggestions !== null
                ? this.stats.pendingSuggestions
                : 0) !==
              0
          "
        >
          <v-chip label color="primary" class="mt-3">
            <v-icon color="white">question_answer</v-icon>
            <v-card-text class="headline"
              >{{
                'Suggestions submitted: ' +
                  ((this.stats.approveSuggestions !== null
                    ? this.stats.approveSuggestions
                    : 0) +
                    (this.stats.rejectedSuggestions !== null
                      ? this.stats.rejectedSuggestions
                      : 0) +
                    (this.stats.pendingSuggestions !== null
                      ? this.stats.pendingSuggestions
                      : 0))
              }}
            </v-card-text>
          </v-chip>

          <GChart
            type="PieChart"
            :data="suggestionChartData"
            :options="chartOptions"
            @ready="getStats"
          />
        </v-card>
        <v-card v-else class="pb-10 " height="100%">
          <v-card-text class="pt-12">NO SUGGESTIONS SUBMITTED</v-card-text>
          <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
        </v-card>
      </v-col>
      <v-col>
        <v-card v-if="this.stats.totalUniqueQuestions !== 0">
          <v-chip label color="primary" class="mt-3">
            <v-icon color="white">assignment</v-icon>
            <v-card-text class="headline"
              >{{ 'Unique questions seen: ' + this.stats.totalUniqueQuestions }}
            </v-card-text>
          </v-chip>
          <GChart
            type="PieChart"
            :data="questionChartData"
            :options="chartOptions"
            @ready="getStats"
          />
        </v-card>
        <v-card v-else class="pb-10 " height="100%">
          <v-card-text class="pt-12">NO QUESTIONS ANSWERED</v-card-text>
          <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
        </v-card>
      </v-col>
      <v-col>
        <v-card v-if="this.correctAnswers !== 0 || this.wrongAnswers !== 0">
          <v-chip label color="primary" class="mt-3">
            <v-icon color="white">assignment</v-icon>
            <v-card-text class="headline"
              >{{ 'Total Quizzes Solved: ' + this.stats.totalQuizzes }}
            </v-card-text>
          </v-chip>
          <GChart
            type="PieChart"
            :data="correctChartData"
            :options="chartOptions"
            @ready="getStats"
          />
        </v-card>
        <v-card v-else class="pb-10 " height="100%">
          <v-card-text class="pt-12">NO QUIZZES SOLVED</v-card-text>
          <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
        </v-card>
      </v-col>
    </v-row>
    <v-row class="d-inline-flex">
      <v-col>
        <v-card v-if="this.stats.tournamentDone !== 0">
          <v-chip label color="primary" class="mt-3">
            <v-icon color="white">fas fa-trophy</v-icon>
            <v-card-text class="headline"
              >{{ 'Total tournaments done: ' + this.stats.tournamentDone }}
            </v-card-text>
          </v-chip>
          <GChart
            type="PieChart"
            :data="tournamentChartData"
            :options="chartOptions"
            @ready="getStats"
          />
        </v-card>
        <v-card v-else class="pb-10 " height="100%">
          <v-card-text class="pt-12">NO TOURNAMENTS DONE</v-card-text>
          <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
        </v-card>
      </v-col>
      <v-col>
        <v-card v-if="this.stats.postSubmitted !== 0">
          <v-chip label color="primary" class="mt-3">
            <v-icon color="white">fas fa-book</v-icon>
            <v-card-text class="headline"
              >{{ 'Total posts submitted: ' + this.stats.postSubmitted }}
            </v-card-text>
          </v-chip>
          <GChart
            type="PieChart"
            :data="postChartData"
            :options="chartOptions"
            @ready="getStats"
          />
        </v-card>
        <v-card v-else class="pb-10 " height="100%">
          <v-card-text class="pt-12">NO POSTS SUBMITTED</v-card-text>
          <v-icon class="pt-6" x-large>fas fa-exclamation-triangle</v-icon>
        </v-card>
      </v-col>
    </v-row>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { GChart } from 'vue-google-charts';
import StudentStats from '@/models/statement/StudentStats';

@Component({
  components: {
    GChart
  }
})
export default class DashboardStatsView extends Vue {
  stats: StudentStats = {
    approveSuggestions: 0,
    correctAnswers: 0,
    improvedCorrectAnswers: 0,
    pendingSuggestions: 0,
    postSubmitted: 0,
    rejectedSuggestions: 0,
    totalAnswers: 0,
    totalAvailableQuestions: 0,
    totalQuizzes: 0,
    totalUniqueQuestions: 0,
    tournamentDone: 0,
    uniqueCorrectAnswers: 0,
    uniqueWrongAnswers: 0
  };
  wrongAnswers: number | null = null;
  correctAnswers: number | null = null;

  suggestionChartData: Array<Object> = [];
  questionChartData: Array<Object> = [];
  correctChartData: Array<Object> = [];
  postChartData: Array<Object> = [];
  tournamentChartData: Array<Object> = [];

  chartOptions = {
    pieHole: 0.7,
    colors: ['red', '#1877d3', '#F4D50E'],
    pieSliceText: 'none'
  };

  async created() {
    this.stats = await RemoteServices.getUserStats();
  }

  async getStats() {
    this.suggestionChartData = [
      ['Title', 'Number'],
      [
        'Number Of Rejected Suggestions',
        this.stats?.rejectedSuggestions !== undefined
          ? this.stats.rejectedSuggestions
          : 0
      ],
      [
        'Number Of Approved Suggestions',
        this.stats?.approveSuggestions !== undefined
          ? this.stats.approveSuggestions
          : 0
      ],
      [
        'Number Of Pending Suggestions',
        this.stats?.pendingSuggestions !== undefined
          ? this.stats.pendingSuggestions
          : 0
      ]
    ];

    this.questionChartData = [
      ['Title', 'Number'],
      [
        'Total Unique Not Answered Question',
        (this.stats?.totalAvailableQuestions !== undefined
          ? this.stats.totalAvailableQuestions
          : 0) -
          (this.stats?.totalUniqueQuestions !== undefined
            ? this.stats.totalUniqueQuestions
            : 0)
      ],
      ['Total Unique Answered Question', this.stats?.totalUniqueQuestions]
    ];

    this.correctAnswers =
      (this.stats?.correctAnswers !== undefined
        ? this.stats.correctAnswers
        : 0) *
      (this.stats?.totalAnswers !== undefined ? this.stats.totalAnswers : 0);

    this.wrongAnswers =
      ((this.stats?.totalAnswers !== undefined ? this.stats.totalAnswers : 0) -
        this.correctAnswers) /
      100;

    this.correctChartData = [
      ['Title', 'Number'],
      [
        'Total Wrong Answers',
        (this.stats?.totalAnswers !== undefined ? this.stats.totalAnswers : 0) -
          ((this.stats?.correctAnswers !== undefined
            ? this.stats.correctAnswers
            : 0) *
            (this.stats?.totalAnswers !== undefined
              ? this.stats.totalAnswers
              : 0)) /
            100
      ],
      [
        'Total Correct Answers',
        ((this.stats?.correctAnswers !== undefined
          ? this.stats.correctAnswers
          : 0) *
          (this.stats?.totalAnswers !== undefined
            ? this.stats.totalAnswers
            : 0)) /
          100
      ]
    ];

    this.postChartData = [
      ['Title', 'Number'],
      ['Upvoted Posts', 7],
      ['Downvoted Posts', 5]
    ];

    this.tournamentChartData = [
      ['Title', 'Number'],
      ['Won Tournaments', 10],
      ['Lost Tournaments', 15]
    ];
  }
}
</script>

<style lang="scss" scoped></style>
