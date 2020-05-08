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
          />

          <v-spacer />
        </v-card-title>
      </template>

      <template v-slot:item._questionStr="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item._questionStr, null)"
          @click="showSuggestionDialog(item)"
      /></template>
      <template v-slot:item._questionStr="{ item }">
        <p
          v-html="convertMarkDown(item._questionStr, null)"
          @click="showSuggestionDialog(item)"
      /></template>

      <!--<template v-slot:item.topics="{ item }">
              <edit-question-topics
                :question="item"
                :topics="topics"
                v-on:question-changed-topics="onQuestionChangedTopics"
               />

            </template>-->

      <template v-slot:item._topicsList="{ item }">
        <v-row justify="space-around">
          <v-chip-group center-active column active-class="primary--text">
            <v-chip v-for="tag in item._topicsList" :key="tag.name">
              {{ tag.name }}
            </v-chip>
          </v-chip-group>
        </v-row>
      </template>

      <!--<template v-slot:item.difficulty="{ item }">
              <v-chip
                v-if="item.difficulty"
                :color="getDifficultyColor(item.difficulty)"
                dark
                >{{ item.difficulty + '%' }}</v-chip
              >
            </template>-->

      <template v-slot:item.status="{ item }">
        <v-chip v-if="item.status" :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
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
              data-cy="showSuggestionButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Suggestion</span>
        </v-tooltip>

        <v-tooltip bottom>
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
        <v-tooltip bottom>
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

        <v-tooltip bottom>
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
        <!--  <v-tooltip bottom>
                   <template v-slot:activator="{ on }">
                     <v-icon
                       small
                       class="mr-2"
                       v-on="on"
                       @click="deleteQuestion(item)"
                       color="red"
                       >delete</v-icon
                     >
                   </template>
                   <span>Delete Question</span>
                 </v-tooltip>-->
      </template>
    </v-data-table>
    <reject-suggestion-dialog
      v-if="currentSuggestion"
      v-model="rejectSuggDialogue"
      :suggestion="currentSuggestion"
      :dialog="rejectSuggDialogue"
      v-on:save-suggestion="onSaveSuggestion"
      v-on:close-rejection="onCloseShowRejectDialog"
    />
    <show-suggestion-dialog
      v-if="currentSuggestion"
      :dialog="questionDialog"
      :suggestion="currentSuggestion"
      v-on:reject-suggestion="RejectSuggestion"
      v-on:close-show-suggestion-dialog="onCloseShowSuggestionDialog"
    />
    <add-question-dialog
      v-if="currentSuggestion"
      v-model="addQuestionDialog"
      :suggestion="currentSuggestion"
      :dialog="addQuestionDialog"
      v-on:save-suggestion="onSaveSuggestion"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';
import Suggestion from '@/models/management/Suggestion';
import EditSuggestionDialog from '@/views/suggestions/EditSuggestionDialog.vue';
import ShowSuggestion from '@/views/suggestions/ShowSuggestion.vue';
import ShowSuggestionDialog from '@/views/suggestions/ShowSuggestionDialog.vue';
import RejectSuggDialogue from '@/views/teacher/Suggestions/RejectSuggDialogue.vue';
import ShowSuggDialog from '@/views/teacher/Suggestions/ShowSuggDialog.vue';
import AddQuestionDialog from '@/views/teacher/Suggestions/AddQuestionDialog.vue';

@Component({
  components: {
    'show-suggestion-dialog': ShowSuggDialog,
    'reject-suggestion-dialog': RejectSuggDialogue,
    'edit-question-topics': EditQuestionTopics,
    'add-question-dialog': AddQuestionDialog
  }
})
export default class SuggestionsView extends Vue {
  suggestions: Suggestion[] = [];
  topics: Topic[] = [];
  currentSuggestion: Suggestion | null = null;
  approveSuggDialog: boolean = false;
  rejectSuggDialogue: boolean = false;
  questionDialog: boolean = false;
  addQuestionDialog: boolean = false;
  search: string = '';
  statusList = ['TOAPPROVE', 'APPROVED', 'REJECTED', 'QUESTION'];

  headers: object = [
    { text: 'Suggestion', value: '_questionStr', align: 'left' },
    {
      text: 'Topics',
      value: '_topicsList',
      align: 'center',
      sortable: false
    },
    { text: 'Status', value: 'status', align: 'center' },

    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center'
    },

    {
      text: 'Quick Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  @Watch('approveSuggDialog')
  closeError() {
    if (!this.approveSuggDialog) {
      this.currentSuggestion = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
      this.suggestions = await RemoteServices.getSuggestions();
      this.suggestions.reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
      (sugg: Suggestion) => sugg._id == suggestionId
    );
    if (sugg) {
      sugg._topicsList = topics;
    }
  }

  /*async setStatus(questionId: number, status: string) {
          try {
            await RemoteServices.setQuestionStatus(questionId, status);
            let question = this.questions.find(
              question => question.id === questionId
            );
            if (question) {
              question.status = status;
            }
          } catch (error) {
            await this.$store.dispatch('error', error);
          }
        }*/

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'TOAPPROVE') return 'yellow';
    else return 'green';
  }

  showSuggestionDialog(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.questionDialog = true;
  }

  onCloseShowSuggestionDialog() {
    this.questionDialog = false;
  }

  showApproveSuggestionDialog(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.approveSuggDialog = true;
  }

  onCloseShowApproveSuggestionDialog() {
    this.approveSuggDialog = false;
  }

  newSuggestion() {
    this.currentSuggestion = new Suggestion();
    this.approveSuggDialog = true;
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
      const result = await RemoteServices.approveSuggestion(sugg);

      this.$emit('approve-question', result);
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
  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return this.convertMarkDownNoFigure(text, image);
  }
  async AddQuestion(sugg: Suggestion) {
    this.currentSuggestion = sugg;
    this.addQuestionDialog = true;
  }

  async onSaveSuggestion(sugg: Suggestion) {
    //this.suggestions = this.suggestions.filter(q => q.id !== sugg.id);
    this.suggestions.unshift(sugg);
    this.rejectSuggDialogue = false;
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

  /*async deleteSuggestion(toDeletequestion: Suggestion) {
          if (
            toDeletequestion.id &&
            confirm('Are you sure you want to delete this question?')
          ) {
            try {
              await RemoteServices.deleteQuestion(toDeletequestion.id);//delete suggestion criar
              this.questions = this.questions.filter(
                question => question.id != toDeletequestion.id
              );
            } catch (error) {
              await this.$store.dispatch('error', error);
            }
          }
        }*/
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
