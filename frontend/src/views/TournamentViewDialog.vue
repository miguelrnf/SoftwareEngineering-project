<template>
  <v-dialog
    width="auto"
    class="test"
    :value="dialog"
    @input="$emit('close-show-tournament-dialog', false)"
    @keydown.esc="$emit('close-show-tournament-dialog', false)"
  >
    <v-card>
      <v-toolbar dense :color="'primary'" :dark="true">
        <v-toolbar-title>
          {{ tournament.title }}
        </v-toolbar-title>
      </v-toolbar>
      <v-row v-for="k in 2" :key="k" :justify="'center'" class="mx-0">
        <v-col v-for="n in 3" :key="n">
          <v-card color="primary">
            <v-row>
              <v-col v-if="n === 1 && k === 1" class="white--text">
                Starts
              </v-col>
              <v-col v-if="n === 2 && k === 1" class="white--text">
                Ends
              </v-col>
              <v-col v-if="n === 3 && k === 1" class="white--text">
                Created
              </v-col>
              <v-col v-if="n === 1 && k === 2" class="white--text">
                Assessment
              </v-col>
              <v-col v-if="n === 2 && k === 2" class="white--text">
                Questions
              </v-col>
              <v-col v-if="n === 3 && k === 2" class="white--text">
                Participants
              </v-col>
            </v-row>
          </v-card>
          <v-card color="secondary">
            <v-row>
              <v-col v-if="n === 1 && k === 1">
                {{ tournament.availableDate }}
              </v-col>
              <v-col v-if="n === 2 && k === 1">
                {{ tournament.conclusionDate }}
              </v-col>
              <v-col v-if="n === 3 && k === 1">
                {{ tournament.creationDate }}
              </v-col>
              <v-col v-if="n === 1 && k === 2">
                {{ tournament.assessmentDto.title }}
              </v-col>
              <v-col v-if="n === 2 && k === 2">
                {{ tournament.numberOfQuestions }}
              </v-col>
              <v-col v-if="n === 3 && k === 2">
                {{ tournament.enrolledStudents.length }}
              </v-col>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
      <v-card-text>
        <div class="text-right">
          by
          <span v-html="convertMarkDown(tournament.owner.username)" />
        </div>
      </v-card-text>

      <v-divider></v-divider>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          v-if="prepareToResults(tournament)"
          class="btn"
          color="primary"
          @click="showResults(tournament)"
        >
          Results
        </v-btn>
        <v-btn
          color="primary"
          text
          v-if="sign !== undefined"
          @click="$emit('sign-in')"
          data-cy="sign"
        >
          {{ sign }}
        </v-btn>
        <v-btn
          dark
          color="primary"
          @click="$emit('close-show-tournament-dialog')"
          data-cy="closeButton"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';
import StatementManager from '@/models/statement/StatementManager';
import User from '@/models/user/User';
import Image from '@/models/management/Image';

@Component
export default class TournamentViewDialog extends Vue {
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;
  @Prop({ type: User, required: false }) readonly student!: User;
  @Prop({ type: Boolean, required: true }) readonly dashboard!: boolean;
  @Prop({ type: String, required: false }) readonly sign!: '';

  async showResults(tournament: Tournament) {
    if (this.dashboard) {
      if (this.student?.username != null) {
        let t = await RemoteServices.getTournament(
          tournament.id,
          this.student.username
        );
        let statementManager: StatementManager = StatementManager.getInstance;

        statementManager.correctAnswers = t.solved.correctAnswers;
        statementManager.statementQuiz = t.solved.statementQuiz;
        await this.$router.push({ name: 'quiz-results' });
      }
    }
  }

  prepareToResults(t: Tournament): boolean {
    return (
      t.completed &&
      t.quiz?.timeToResults != null &&
      t.quiz?.timeToResults <= 0 &&
      this.dashboard
    );
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped></style>
