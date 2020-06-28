<template>
  <v-dialog
    :value="dialog"
    @input="closeDialog"
    @keydown.esc="closeDialog"
    max-width="80%"
  >
    <v-card class="mx-auto">
      <v-card-title>Evaluation Settings</v-card-title>

      <v-container fluid>
        <v-row align="center" class="mx-0">
          <v-col cols="6">
            <v-subheader>Scale (0 - X)</v-subheader>
          </v-col>

          <v-col cols="6">
            <v-select
              v-model="select"
              :items="items"
              item-text="state"
              item-value="abbr"
              label="Select"
              persistent-hint
              return-object
              single-line
            />
          </v-col>
        </v-row>
      </v-container>

      <v-subheader>Evaluation percentages:</v-subheader>
      <br />
      <v-card-text class="pt-0">
        <v-slider
          v-model="quizzes"
          :rules="quizRules"
          label="Evaluation Quizzes (%)"
          step="5"
          thumb-label="always"
          ticks
        />
      </v-card-text>

      <v-card-text class="pt-0">
        <v-slider
          v-model="tournament"
          :rules="tournamentRules"
          label="Tournaments (%)"
          persistent-hint
          step="5"
          thumb-label="always"
          ticks
        />
      </v-card-text>

      <v-card-text class="pt-0">
        <v-slider
          v-model="suggestions"
          :rules="suggestionRules"
          label="Approved Suggestions (%)"
          persistent-hint
          step="5"
          thumb-label="always"
          ticks
        />
      </v-card-text>

      <v-subheader inset v-if="suggestions + tournament + quizzes < 100"
        >{{ 100 - (suggestions + tournament + quizzes) }} % left to
        assign</v-subheader
      >

      <v-card-actions>
        <v-spacer />
        <v-btn color="primary" text @click="closeDialog" data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn color="primary" text @click="save" data-cy="save">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Vue, Watch } from 'vue-property-decorator';
import EvalSettings from '@/models/management/EvalSettings';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditEvalSettingsDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  quizzes: number = 0;
  suggestions: number = 0;
  tournament: number = 0;

  settings: EvalSettings | null = null;

  items: Array<{ state: string; abbr: number }> | undefined = [];

  select: { state: string; abbr: number } | undefined = { state: ' ', abbr: 0 };

  quizRules: Array<Object> | undefined;
  tournamentRules: Array<Object> | undefined;
  suggestionRules: Array<Object> | undefined;

  async created() {
    this.settings = await RemoteServices.getEvalSettings();
    this.quizzes = this.settings.quizWeight;
    this.tournament = this.settings.tournamentWeight;
    this.suggestions = this.settings.suggWeight;
    this.items = [
      {
        state: '0 - 20',
        abbr: 20
      },
      {
        state: '0 - 100',
        abbr: 100
      },
      {
        state: '0 - 10',
        abbr: 10
      },
      {
        state: '0 - 5',
        abbr: 5
      }
    ];
    for (let i = 0; i < this.items.length; i++) {
      if (this.settings.scale == this.items[i].abbr) {
        this.select = this.items[i];
        break;
      }
    }

    this.calculateSuggestionRules();
    this.calculateQuizRules();
    this.calculateTournamentRules();
  }

  @Watch('quizzes', { immediate: true, deep: true })
  calculateQuizRules() {
    this.quizRules = [
      this.tournament + this.suggestions + this.quizzes <= 100 ? true : 'Max'
    ];
  }

  @Watch('suggestions', { immediate: true, deep: true })
  calculateSuggestionRules() {
    this.suggestionRules = [
      this.tournament + this.suggestions + this.quizzes <= 100 ? true : 'Max'
    ];
  }

  @Watch('tournament', { immediate: true, deep: true })
  calculateTournamentRules() {
    this.tournamentRules = [
      this.tournament + this.suggestions + this.quizzes <= 100 ? true : 'Max'
    ];
  }

  closeDialog() {
    this.$emit('close-edit-lecture-dialog');
  }

  async save() {
    let isValid = true;
    if (this.settings != null) {
      if (this.quizzes + this.suggestions + this.tournament == 100) {
        this.settings.quizWeight = this.quizzes;
        this.settings.suggWeight = this.suggestions;
        this.settings.tournamentWeight = this.tournament;
      } else isValid = false;
      if (this.select?.abbr != undefined && this.select.abbr != 0)
        this.settings.scale = this.select.abbr;
      else isValid = false;
    }

    if (isValid && this.settings != null) {
      await RemoteServices.changeEvalSettings(this.settings);
      this.$emit('close-edit-lecture-dialog');
    } else if (this.settings != null) {
      await this.$store.dispatch('error', 'Error: Invalid settings');
    }
  }
}
</script>

<style lang="scss" scoped />
