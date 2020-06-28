<template>
  <v-card class="table">
    <v-card-text>
      <div>My self-study menu</div>
    </v-card-text>

    <v-tabs vertical>
      <v-tabs-slider color="primary" />

      <v-tab>
        <v-icon left>fab fa-leanpub</v-icon>
        Learn More
      </v-tab>
      <v-tab>
        <v-icon left>fas fa-file-contract</v-icon>
        Create Summary
      </v-tab>

      <v-tab-item>
        <v-card flat>
          <v-card-text>
            <p>
              Here you can learn more about a certain topic, and create a quiz
              to test your knowledge. You can also access your previous created
              quizzes about each Topic and request a suggested quiz, based in
              your progress.
            </p>
          </v-card-text>

          <v-btn
            text
            color="primary"
            small
            :loading="loading"
            :disabled="loading"
            @click="loader"
          >
            Show Suggested Quiz
          </v-btn>
          <span v-show="!hidden">
            -
          </span>
          <v-btn v-show="!hidden" text small @click="createQuiz(suggestedQuiz)"
            >{{ suggestedQuiz }}
          </v-btn>

          <v-container fluid>
            <v-row align="center">
              <v-col>
                <v-select
                  v-model="topicName"
                  :items="availableTopics"
                  item-text="name"
                  menu-props="auto"
                  label="Select Topic and Create Quiz"
                  hide-details
                  prepend-icon="map"
                  single-line
                  @change="createQuiz(topicName)"
                  :statementManager1="statementManager"
                >
                </v-select>
              </v-col>
            </v-row>
          </v-container>

          <br />

          <v-card flat class="mx-auto">
            <v-toolbar height="37" color="primary" dark>
              <v-toolbar-title>My Topic Quizzes</v-toolbar-title>

              <v-spacer />

              <v-btn small icon>
                <v-icon>mdi-magnify</v-icon>
              </v-btn>
            </v-toolbar>

            <v-list two-line flat>
              <v-list-item-group multiple>
                <template>
                  <v-list-item
                    v-for="item in quizzes"
                    :key="item.statementQuiz.id"
                  >
                    <template v-slot:default="{ active }">
                      <v-list-item-content @click="show(item)">
                        <v-list-item-title v-text="item.statementQuiz.title" />
                        <v-list-item-subtitle v-text="item.answerDate" />
                      </v-list-item-content>

                      <v-list-item-action>
                        <v-icon v-if="!active" color="grey lighten-1">
                          star_border
                        </v-icon>

                        <v-icon v-else color="yellow">
                          star
                        </v-icon>
                      </v-list-item-action>
                    </template>
                  </v-list-item>
                </template>
              </v-list-item-group>
            </v-list>
          </v-card>
        </v-card>
      </v-tab-item>
      <v-tab-item>
        <v-card flat>
          <v-card-text>
            <p>
              Here you can create a summary with all the questions that you have
              seen, which are related to one or more topics that you choose.
            </p>

            <v-card-text>
              <v-autocomplete
                v-model="selectedTopics"
                :items="availableTopics"
                multiple
                return-object
                item-text="name"
                item-value="name"
                label="Topics"
                outlined
                data-cy="topics"
              >
                <template v-slot:selection="data">
                  <v-chip
                    v-bind="data.attrs"
                    :input-value="data.selected"
                    @click="data.select"
                    close
                    @click:close="removeTopic(data.item)"
                  >
                    {{ data.item.name }}
                  </v-chip>
                </template>
                <template v-slot:item="data">
                  <v-list-item-content>
                    <v-list-item-title v-html="data.item.name" />
                  </v-list-item-content>
                </template>
              </v-autocomplete>

              <v-btn
                color="primary"
                elevation="5"
                @click="newSummary"
                data-cy="createButton"
                >Create</v-btn
              >
            </v-card-text>
          </v-card-text>
        </v-card>
      </v-tab-item>
    </v-tabs>

    <show-summary-dialog
      v-if="summaryDialog"
      :dialog="summaryDialog"
      :topics="selectedTopics"
      :availableTopics="availableTopics"
      :statementManager="statementManager"
      v-on:close-show-summary-dialog="onCloseShowSummaryDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import ShowSummaryDialog from '@/views/student/study/ShowSummaryDialog.vue';
import StatementManager from '@/models/statement/StatementManager';
import SolvedQuiz from '@/models/statement/SolvedQuiz';

@Component({
  components: {
    'show-summary-dialog': ShowSummaryDialog
  }
})
export default class StudyHomeView extends Vue {
  topics: Topic[] = [];
  availableTopics: Topic[] = [];
  summaryDialog: boolean = false;

  selectedTopics: Topic[] = [];

  topicName: String = '';

  private loading: boolean = false;
  private hidden: boolean = true;

  statementManager: StatementManager = StatementManager.getInstance;

  suggestedQuiz: String = '';

  quizzes: SolvedQuiz[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.statementManager.reset();

      this.availableTopics = await RemoteServices.getAvailableTopics();

      this.quizzes = (await RemoteServices.getMyOwnTopicQuizzes()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async createQuiz(topic: String) {
    try {
      await this.statementManager.getTopicQuizStatement(topic);
      await this.$router.push({ path: '/student/quiz' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  removeTopic(topic: Topic) {
    this.selectedTopics = this.selectedTopics.filter(
      element => element.id != topic.id
    );
  }

  onCloseShowSummaryDialog() {
    this.summaryDialog = false;
  }

  newSummary() {
    this.summaryDialog = true;
  }

  data() {
    return {
      hidden: true
    };
  }

  async loader() {
    try {
      this.loading = true;
      setTimeout(() => ((this.loading = false), (this.hidden = false)), 2500);
      this.suggestedQuiz = await RemoteServices.getSuggestTopicQuiz();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async show(quiz: SolvedQuiz) {
    this.statementManager.correctAnswers = quiz.correctAnswers;
    this.statementManager.statementQuiz = quiz.statementQuiz;
    await this.$router.push({ path: '/student/results' });
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
