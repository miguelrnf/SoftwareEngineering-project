<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="suggestions"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            data-cy="search"
          />

          <v-spacer />
          <v-btn
            v-if="!isTeacher()"
            color="primary"
            dark
            @click="newSuggestion"
            data-cy="createButton"
            >New Suggestion</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:item.studentQuestion="{ item }">
        <p
          v-html="convertMarkDown(item.studentQuestion, null)"
          @click="showSuggestionDialog(item)"
      /></template>
      <template v-slot:item._topicsList="{ item }">
        <v-chip v-for="tag in item.topicsList" :key="tag.name" class="ma-1">
          {{ tag.name }}
        </v-chip>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip v-if="item.status" :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

      <template v-slot:item.isPrivate="{ item }">
        <v-chip :color="getPrivacyColor(item.isPrivate)" small>
          <span class="white--text">{{ getPrivacyTag(item.isPrivate) }}</span>
        </v-chip>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="showSuggestionDialog(item)"
              data-cy="showSuggButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isOwner(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="editSuggestion(item)"
              data-cy="editSuggButton"
              >edit</v-icon
            >
          </template>
          <span>Edit Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="duplicateSuggestion(item)"
              >cached</v-icon
            >
          </template>
          <span>Duplicate Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isTeacher()">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="ApproveSuggestion(item)"
              data-cy="quickApproveButton"
              >done</v-icon
            >
          </template>
          <span>Approve Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isTeacher()">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="RejectSuggestion(item)"
              data-cy="quickRejectButton"
              >highlight_off</v-icon
            >
          </template>
          <span>Reject Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom v-if="isTeacher()">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="AddQuestion(item)"
              data-cy="addQuestionButton"
              >fas fa-book-medical</v-icon
            >
          </template>
          <span>Change to Question</span>
        </v-tooltip>
        <v-tooltip bottom>
           <template v-slot:activator="{ on }">
             <v-icon
               small
               class="mr-2"
               v-on="on"
               @click="deleteSuggestion(item)"
               color="red"
               >delete</v-icon
             >
           </template>
           <span>Delete Suggestion</span>
         </v-tooltip>
      </template>
      <template v-slot:item.checkMark="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
                    :color="getCheckMarkColor(item.checkMark)"
                    small
                    class="mr-2"
                    v-on="on"

            >fas fa-check</v-icon
            >
          </template>
          <span> {{ getCheckMarkTip(item.checkMark) }}</span>
        </v-tooltip>
        </template>
    </v-data-table>
    <edit-suggestion-dialog
      v-if="currentSuggestion && editSuggestionDialog"
      v-model="editSuggestionDialog"
      :suggestion="currentSuggestion"
      :topics="topics"
      :dialog="editSuggestionDialog"
      v-on:save-suggestion="onSaveSuggestion"
    />
    <show-suggestion-dialog
      v-if="currentSuggestion && questionDialog"
      :dialog="questionDialog"
      :suggestion="currentSuggestion"
      v-on:close-show-suggestion-dialog="onCloseShowSuggestionDialog"
    />
    <add-question-dialog
      v-if="currentSuggestion && addQuestionDialog"
      v-model="addQuestionDialog"
      :suggestion="currentSuggestion"
      :dialog="addQuestionDialog"
      v-on:save-suggestion="onSaveSuggestion"
      v-on:close-add-question-dialog="onCloseAddQuestionDialog"
    />
    <reject-suggestion-dialog
      v-if="currentSuggestion && rejectSuggDialogue"
      v-model="rejectSuggDialogue"
      :suggestion="currentSuggestion"
      :dialog="rejectSuggDialogue"
      v-on:save-suggestion="onRejectSuggestion"
      v-on:close-rejection="onCloseShowRejectDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';
import Suggestion from '@/models/management/Suggestion';
import EditSuggestionDialog from '@/views/suggestions/EditSuggestionDialog.vue';
import ShowSuggestionDialog from '@/views/ShowSuggestionDialog.vue';
import AddQuestionDialog from '@/views/teacher/suggestions/AddQuestionDialog.vue';
import RejectSuggestionDialog from '@/views/teacher/suggestions/RejectSuggestionDialog.vue';

@Component({
  components: {
    'show-suggestion-dialog': ShowSuggestionDialog,
    'edit-suggestion-dialog': EditSuggestionDialog,
    'edit-question-topics': EditQuestionTopics,
    'reject-suggestion-dialog': RejectSuggestionDialog,
    'add-question-dialog': AddQuestionDialog
  }
})
export default class SuggestionsView extends Vue {
  suggestions: Suggestion[] = [];
  topics: Topic[] = [];
  currentSuggestion: Suggestion | null = null;
  editSuggestionDialog: boolean = false;
  questionDialog: boolean = false;
  search: string = '';
  addQuestionDialog: boolean = false;
  rejectSuggDialogue: boolean = false;

  headers: object = [
    { text: 'Suggestion', value: 'studentQuestion', align: 'left' },
    {
      text: 'Topics',
      value: '_topicsList',
      align: 'center',
      sortable: false
    },

    { text: 'Privacy', value: 'isPrivate', align: 'center' },

    { text: 'Status', value: 'status', align: 'center' },

    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center'
    },

    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    },
    {
      text: 'Seen',
      value:'checkMark',
      align: 'center'
    }

  ];

  @Watch('editSuggestionDialog')
  closeError() {
    if (!this.editSuggestionDialog) {
      this.currentSuggestion = null;
    }
  }

  async created() {
    this.topics = await RemoteServices.getTopics();
    this.suggestions = await RemoteServices.getSuggestions();
  }

  customFilter(value: string, search: string, suggestion: Suggestion) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(suggestion)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  onSuggestionTopics(suggestionId: Number, topics: Topic[]) {
    let sugg = this.suggestions.find(
      (sugg: Suggestion) => sugg.id == suggestionId
    );
    if (sugg) {
      sugg.topicsList = topics;
    }
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'TOAPPROVE') return 'yellow';
    else return 'green';
  }

  getPrivacyColor(isprivate: boolean) {
    if (isprivate) return 'black';
    else return 'orange';
  }

  getCheckMarkColor(checkMark: boolean) {
    if (checkMark) return 'blue';
    else return 'grey';
  }

  getCheckMarkTip(checkMark: boolean) {
    if (checkMark) return 'Seen';
    else return 'Not Seen';
  }

  getPrivacyTag(isprivate: boolean) {
    if (isprivate) return 'PRIVATE';
    else return 'PUBLIC';
  }

  showSuggestionDialog(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.questionDialog = true;
  }

  onCloseShowSuggestionDialog() {
    this.questionDialog = false;
  }

  newSuggestion() {
    this.currentSuggestion = new Suggestion();
    this.editSuggestionDialog = true;
  }

  editSuggestion(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.editSuggestionDialog = true;
  }

  duplicateSuggestion(sugg: Suggestion) {
    this.currentSuggestion = new Suggestion(sugg);
    this.currentSuggestion.id = null;
    this.editSuggestionDialog = true;
  }

  async onRejectSuggestion() {
    this.currentSuggestion = null;
    this.rejectSuggDialogue = false;
  }

  async onSaveSuggestion(sugg: Suggestion) {
    this.suggestions.unshift(sugg);
    this.editSuggestionDialog = false;
    this.addQuestionDialog = false;
    this.currentSuggestion = null;
  }

  async exportCourseQuestions() {
    let fileName = this.$store.getters.getCurrentCourse.name + '-Questions.zip';
    try {
      let result = await RemoteServices.exportCourseQuestions();
      const url = window.URL.createObjectURL(result);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  isOwner(suggestion: Suggestion): boolean {
    if (this.$store.getters.getUser != null) {
      return (
        this.$store.getters.getUser.username === suggestion.student?.username
      );
    } else return false;
  }

  isTeacher(): boolean {
    return this.$store.getters.isTeacher;
  }

  async AddQuestion(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.addQuestionDialog = true;
  }

  async ApproveSuggestion(sugg: Suggestion) {
    if (sugg && sugg.status == 'REJECTED') {
      await this.$store.dispatch(
        'error',
        'You can not approve a rejected suggestion before the students edits it'
      );
    } else if (sugg && sugg.status == 'APPROVED') {
      await this.$store.dispatch(
        'error',
        'You can not approve a suggestion twice'
      );
    } else {
      sugg.status = 'APPROVED';
      await RemoteServices.approveSuggestion(sugg);
    }
  }

  async RejectSuggestion(sugg: Suggestion) {
    if (sugg != null) {
      this.currentSuggestion = sugg;
    }

    if (sugg && sugg.status == 'REJECTED') {
      await this.$store.dispatch(
        'error',
        'You can not reject a question twice'
      );
    } else if (sugg && sugg.status == 'APPROVED') {
      await this.$store.dispatch(
        'error',
        'You can not reject an approved suggestion'
      );
    } else {
      this.rejectSuggDialogue = true;
    }
  }

  onCloseShowRejectDialog() {
    this.rejectSuggDialogue = false;
  }

  onCloseAddQuestionDialog() {
    this.addQuestionDialog = false;
  }

  async deleteSuggestion(suggestion: Suggestion) {
    if (
      suggestion.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteSuggestion(suggestion.id);//delete suggestion criar
        this.suggestions = this.suggestions.filter(
          sugg => sugg.id != suggestion.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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
