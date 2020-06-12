<template>
  <v-dialog
    width="auto"
    :value="editDialog"
    max-width="60%"
    @input="$emit('close-show-edit-tournament-dialog')"
    @keydown.esc="$emit('close-show-edit-tournament-dialog')"
  >
    <v-card class="test">
      <v-container>
        <v-card-title class="justify-center">Edit Tournament</v-card-title>
      </v-container>
      <v-card-text>
        <v-text-field v-model="editedTitle" label="*Title" data-cy="title" />
      </v-card-text>
      <v-container>
        <p class="pl-0">Type</p>
        <v-btn-toggle v-model="editedType" mandatory class="button-group">
          <v-btn text value="STANDARD">STANDARD</v-btn>
        </v-btn-toggle>
      </v-container>
      <v-container>
        <p>Assessment</p>
        <v-btn-toggle
          v-if="availableAssessments.length > 0"
          v-model="editedAssessmentDto.id"
          mandatory
          class="button-group"
        >
          <v-btn
            v-for="assessment in availableAssessments"
            text
            :value="assessment.id"
            :key="assessment.id"
            data-cy="AssessmentTitle"
            >{{ assessment.title }}</v-btn
          >
        </v-btn-toggle>
        <v-card-text v-if="availableAssessments.length === 0" class="message">
          {{ 'NO ASSESSMENTS FOUND' }}
        </v-card-text>
      </v-container>
      <v-container>
        <p class="pl-0">Number of Questions</p>
        <v-btn-toggle
          v-model="editedNumberOfQuestions"
          mandatory
          class="button-group"
        >
          <v-btn text value="10">10</v-btn>
          <v-btn text value="20">20</v-btn>
          <v-btn text value="30">30</v-btn>
          <v-btn text value="40">40</v-btn>
        </v-btn-toggle>
      </v-container>
      <v-container fluid>
        <v-row>
          <v-col cols="12" sm="6">
            <VueCtkDateTimePicker
              label="*Available Date"
              id="availableDateInput"
              v-model="avalDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
              data-cy="availableDate"
              :dark="isDark"
              :color="$vuetify.theme.themes.light.primary"
            >
            </VueCtkDateTimePicker>
          </v-col>
          <v-spacer></v-spacer>
          <v-col cols="12" sm="6">
            <VueCtkDateTimePicker
              label="*Conclusion Date"
              id="conclusionDateInput"
              v-model="concDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
              data-cy="conclusionDate"
              :dark="isDark"
              :color="$vuetify.theme.themes.light.primary"
            >
            </VueCtkDateTimePicker>
          </v-col>
        </v-row>
      </v-container>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          @click="$emit('close-show-edit-tournament-dialog')"
          data-cy="cancelButton"
          >Close</v-btn
        >
        <v-btn
          v-if="canSave"
          @click="saveTournamentEdit"
          depressed
          color="primary"
          data-cy="editTournament"
        >
          Edit Tournament
        </v-btn>
        <v-btn
          v-else
          @click="saveTournamentEdit"
          depressed
          color="primary"
          data-cy="editTournament"
          disabled
        >
          Edit Tournament
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Assessment from '@/models/management/Assessment';
import RemoteServices from '@/services/RemoteServices';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import { Tournament } from '@/models/management/Tournament';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component
export default class CreateTournamentView extends Vue {
  @Model('editDialog', Boolean) editDialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;
  availableAssessments: Assessment[] = [];
  avalDate: string = '';
  concDate: string = '';
  editedTitle: string = '';
  editedNumberOfQuestions: number = 0;
  editedAssessmentDto: Assessment = new Assessment();
  editedType: string = '';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.availableAssessments = await RemoteServices.getAvailableAssessments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  get canSave(): boolean {
    return (
      !!this.editedTitle &&
      !!this.avalDate &&
      !!this.concDate &&
      this.editedTitle.trim() != ''
    );
  }

  get isDark(): boolean {
    return this.$vuetify.theme.dark;
  }

  async saveTournamentEdit() {
    try {
      let editedTournament = new Tournament(this.tournament);
      editedTournament.title = this.editedTitle;
      editedTournament.assessmentDto = this.editedAssessmentDto;
      editedTournament.availableDate = this.avalDate;
      editedTournament.conclusionDate = this.concDate;
      editedTournament.type = this.editedType;
      editedTournament.numberOfQuestions = this.editedNumberOfQuestions;
      const result = await RemoteServices.editTournament(editedTournament);
      this.$emit('save-tournament', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style lang="scss" scoped>
.message {
  font-weight: bold;
}
.test {
  margin: 0;
}

.button-group {
  flex-wrap: wrap;
  justify-content: center;
}
</style>
