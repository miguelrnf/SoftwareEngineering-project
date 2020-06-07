<template style="height: 100%">
  <v-card
    max-height="250%"
    min-height="100%"
    class="quiz-container overflow-x-hidden"
    @keydown.right="confirmAnswer"
    @keydown.left="decreaseOrder"
    v-if="!confirmed"
  >
    <v-card height="50px" color="accent">
      <v-row :justify="'space-between'">
        <v-col md="auto" class="pa-3">
          <span
            class="timer"
            @click="hideTime = !hideTime"
            v-if="statementQuiz && statementQuiz.timeToSubmission"
          >
            <i class="fas fa-clock"></i>
            <span v-if="!hideTime">{{ submissionTimer }}</span>
          </span>
        </v-col>
        <v-col md="auto" class="pa-0 mr-2">
          <v-card
            tile
            class="white--text pa-3"
            style="font-size: large; font-weight: bold"
            color="accent darken-1"
            @click="confirmationDialog = true"
            data-cy="endQuizButton"
            max-height="50px"
          >
            <v-icon color="white">fas fa-times</v-icon>
            End Quiz
          </v-card>
        </v-col>
      </v-row>
    </v-card>

    <v-card height="45px" class="question-navigation">
      <v-row :justify="'center'" class="navigation-buttons">
        <v-col
          md="auto"
          v-bind:class="'question-button'"
          v-for="index in +statementQuiz.questions.length"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          <v-card
            tile
            :color="index === questionOrder + 1 ? 'secondary' : 'accent'"
          >
            {{ index }}
          </v-card>
        </v-col>
      </v-row>
      <v-icon
        class="left-button"
        @click="decreaseOrder"
        color="secondary"
        x-large
        v-if="questionOrder !== 0 && !statementQuiz.oneWay"
        >fas fa-chevron-left</v-icon
      >
      <v-icon
        class="right-button"
        @click="confirmAnswer"
        color="secondary"
        x-large
        v-if="questionOrder !== statementQuiz.questions.length - 1"
        >fas fa-chevron-right</v-icon
      >
    </v-card>
    <question-component
      v-model="questionOrder"
      v-if="statementQuiz.answers[questionOrder]"
      :optionId="statementQuiz.answers[questionOrder].optionId"
      :question="statementQuiz.questions[questionOrder]"
      :questionNumber="statementQuiz.questions.length"
      :backsies="!statementQuiz.oneWay"
      @increase-order="confirmAnswer"
      @select-option="changeAnswer"
      @decrease-order="decreaseOrder"
    />

    <v-dialog v-model="confirmationDialog" width="50%">
      <v-card>
        <v-card-title primary-title class="secondary white--text headline">
          Confirmation
        </v-card-title>

        <v-card-text class="text--black title">
          <br />
          Are you sure you want to finish?
          <br />
          <span
            v-if="
              statementQuiz.answers
                .map(answer => answer.optionId)
                .filter(optionId => optionId == null).length
            "
          >
            You still have
            {{
              statementQuiz.answers
                .map(answer => answer.optionId)
                .filter(optionId => optionId == null).length
            }}
            unanswered questions!
          </span>
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn color="secondary" text @click="confirmationDialog = false">
            Cancel
          </v-btn>
          <v-btn
            color="primary"
            text
            @click="concludeQuiz"
            data-cy="imSureButton"
          >
            I'm sure
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="nextConfirmationDialog" width="50%">
      <v-card>
        <v-card-title primary-title class="secondary white--text headline">
          Confirmation
        </v-card-title>

        <v-card-text class="text--black title">
          <br />
          Are you sure you want to go to the next question?
          <br />
        </v-card-text>

        <v-divider />

        <v-card-actions>
          <v-spacer />
          <v-btn color="secondary" text @click="nextConfirmationDialog = false">
            Cancel
          </v-btn>
          <v-btn color="primary" text @click="increaseOrder">
            I'm sure
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>

  <div class="container" v-else-if="statementQuiz.timeToResults">
    <v-card>
      <v-card-title class="justify-center">
        Hold on and wait {{ resultsTimer }} to view the results
      </v-card-title>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import QuestionComponent from '@/views/student/quiz/QuestionComponent.vue';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';
import StatementQuiz from '@/models/statement/StatementQuiz';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component({
  components: {
    'question-component': QuestionComponent
  }
})
export default class QuizView extends Vue {
  @Prop({ type: StatementManager, required: false })
  readonly statementManager1!: StatementManager;
  statementManager: StatementManager = StatementManager.getInstance;
  statementQuiz: StatementQuiz | null =
    StatementManager.getInstance.statementQuiz;
  confirmationDialog: boolean = false;
  confirmed: boolean = false;
  nextConfirmationDialog: boolean = false;
  startTime: Date = new Date();
  questionOrder: number = 0;
  hideTime: boolean = false;
  submissionTimer: string = '';
  resultsTimer: string = '';

  async created() {
    if (this.statementManager1) {
      this.statementManager = this.statementManager1;
      this.statementQuiz = this.statementManager1.statementQuiz;
    } else {
      if (!this.statementQuiz?.id) {
        await this.$router.push({ path: '/student/create' });
      } else {
        try {
          await RemoteServices.startQuiz(this.statementQuiz?.id);
        } catch (error) {
          await this.$store.dispatch('error', error);
          await this.$router.push({ path: '/student/available-quizzes' });
        }
      }
    }
  }

  increaseOrder(): void {
    if (this.questionOrder + 1 < +this.statementQuiz!.questions.length) {
      this.calculateTime();
      this.questionOrder += 1;
    }
    this.nextConfirmationDialog = false;
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0 && !this.statementQuiz?.oneWay) {
      this.calculateTime();
      this.questionOrder -= 1;
    }
  }

  changeOrder(newOrder: number): void {
    if (!this.statementQuiz?.oneWay) {
      if (newOrder >= 0 && newOrder < +this.statementQuiz!.questions.length) {
        this.calculateTime();
        this.questionOrder = newOrder;
      }
    }
  }

  async changeAnswer(optionId: number) {
    if (this.statementQuiz && this.statementQuiz.answers[this.questionOrder]) {
      let previousAnswer = this.statementQuiz.answers[this.questionOrder]
        .optionId;
      try {
        this.calculateTime();

        if (
          this.statementQuiz.answers[this.questionOrder].optionId === optionId
        ) {
          this.statementQuiz.answers[this.questionOrder].optionId = null;
        } else {
          this.statementQuiz.answers[this.questionOrder].optionId = optionId;
        }

        await RemoteServices.submitAnswer(
          this.statementQuiz.id,
          this.statementQuiz.answers[this.questionOrder]
        );
      } catch (error) {
        this.statementQuiz.answers[
          this.questionOrder
        ].optionId = previousAnswer;

        await this.$store.dispatch('error', error);
      }
    }
  }

  confirmAnswer() {
    if (this.statementQuiz?.oneWay) {
      this.nextConfirmationDialog = true;
    } else {
      this.increaseOrder();
    }
  }

  @Watch('statementQuiz.timeToSubmission')
  submissionTimerWatcher() {
    if (!!this.statementQuiz && this.statementQuiz.timeToSubmission === 0) {
      this.concludeQuiz();
    }

    this.submissionTimer = milisecondsToHHMMSS(
      this.statementQuiz?.timeToSubmission
    );
  }

  @Watch('statementQuiz.timeToResults')
  resultsTimerWatcher() {
    if (!!this.statementQuiz && this.statementQuiz.timeToResults === 0) {
      this.concludeQuiz();
    }

    this.resultsTimer = milisecondsToHHMMSS(this.statementQuiz?.timeToResults);
  }

  async concludeQuiz() {
    await this.$store.dispatch('loading');
    try {
      this.calculateTime();
      this.confirmed = true;
      await this.statementManager.concludeQuiz();

      await this.$store.dispatch('updateScore');

      if (
        !this.statementQuiz?.timeToResults &&
        this.statementManager.correctAnswers.length !== 0
      ) {
        await this.$router.push({ path: '/student/results' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  calculateTime() {
    if (this.statementQuiz) {
      this.statementQuiz.answers[this.questionOrder].timeTaken +=
        new Date().getTime() - this.startTime.getTime();
      this.startTime = new Date();
    }
  }
}
</script>
<style lang="scss" scoped></style>
