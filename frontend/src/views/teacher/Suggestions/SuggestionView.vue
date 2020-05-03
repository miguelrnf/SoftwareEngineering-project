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
    <approve-suggestion-dialog
      v-if="currentSuggestion"
      v-model="approveSuggDialog"
      :suggestion="currentSuggestion"
      :topics="topics"
      :dialog="approveSuggDialog"
      v-on:save-suggestion="onSaveSuggestion"
    />
    <show-suggestion-dialog
      v-if="currentSuggestion"
      :dialog="questionDialog"
      :suggestion="currentSuggestion"
      v-on:close-show-suggestion-dialog="onCloseShowSuggestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';
import Suggestion from '@/models/management/Suggestion';
import ApproveSuggDialogue from '@/views/teacher/Suggestions/ApproveSuggDialogue.vue';
import ShowSuggDialog from '@/views/teacher/Suggestions/ShowSuggDialog.vue';

@Component({
  components: {
    'show-suggestion-dialog': ShowSuggDialog,
    'approve-suggestion-dialog': ApproveSuggDialogue,
    'edit-question-topics': EditQuestionTopics
  }
})
export default class SuggestionsView extends Vue {
  suggestions: Suggestion[] = [];
  topics: Topic[] = [];
  currentSuggestion: Suggestion | null = null;
  approveSuggDialog: boolean = false;
  questionDialog: boolean = false;
  search: string = '';
  statusList = ['TOAPPROVE', 'APPROVED', 'REJECTED'];

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

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
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
    sugg.status = 'APPROVED';
    const result = await RemoteServices.approveSuggestion(sugg);
    this.$emit('approve-question', result);
  }

  async RejectSuggestion(sugg: Suggestion) {
    sugg.status = 'REJECTED';
    sugg._justification = 'No justification was given';
    const result = await RemoteServices.approveSuggestion(sugg);
    this.$emit('approve-question', result);
  }

  async onSaveSuggestion(sugg: Suggestion) {
    //this.suggestions = this.suggestions.filter(q => q.id !== sugg.id);
    this.suggestions.unshift(sugg);
    this.approveSuggDialog = false;
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

  //TODO ???n sei se e preciso

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
