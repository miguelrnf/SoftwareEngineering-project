<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"



  >
    <v-card class="px-12 ma-0 table"  height="60%">
      <v-row>

        <v-card-title color="primary" class="mb-5 table" >
          <v-icon left>fas fa-check-square</v-icon>
          QUIZZES
        </v-card-title>

      </v-row>
      <v-row>

        <v-card-text>
          <v-autocomplete
                  v-model="selectedQuizzes"
                  :items="availableQuizzes"
                  multiple
                  return-object
                  item-text="title"
                  item-value="title"
                  label="Quizzes"
                  outlined
                  @change="saveQuizzes"

          >
            <template v-slot:selection="data">
              <v-chip
                      v-bind="data.attrs"
                      :input-value="data.selected"
                      close
                      @click="data.select"
                      @click:close="removeQuiz(data.item)"
              >
                {{ data.item.title }}
              </v-chip>
            </template>
            <template v-slot:item="data">
              <v-list-item-content>
                <v-list-item-title v-html="data.item.title" />
              </v-list-item-content>
            </template>
          </v-autocomplete>
        </v-card-text>
      </v-row>
      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          text
          @click="$emit('dialog', false)"
          data-cy="cancel"
          >Cancel</v-btn
        >
        <v-btn
          color="primary"
          text
          @click="saveQuizzesList"
          data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>


    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Suggestion from '@/models/management/Suggestion';
import Topic from '@/models/management/Topic';
import ToggleButton from 'vue-js-toggle-button';
import User from '@/models/user/User';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ShowSuggestion from '@/views/ShowSuggestion.vue';
import VueYouTubeEmbed, { getIdFromURL } from 'vue-youtube-embed';
import LazyYoutubeVideo from 'vue-lazy-youtube-video'
import 'vue-lazy-youtube-video/dist/style.css'
import Classroom from '@/models/management/Classroom';
import Document from '@/models/management/Document';
import StatementQuiz from '@/models/statement/StatementQuiz';


Vue.use(ToggleButton);

@Component({
  components: {

  },


})
export default class EditSelectedQuizzesDialog extends Vue {

  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StatementQuiz, required: true }) readonly availableQuizzes!: StatementQuiz[];
  @Prop({ type: Classroom, required: true }) readonly lecture!: Classroom;

  selectedQuizzes: StatementQuiz[] = JSON.parse(
          JSON.stringify(this.lecture.quizzes)
  );

  async created() {

  }

  async saveQuizzesList() {

      try {
        const result = await RemoteServices.addQuizzes(
                this.lecture
        );
        this.$emit('save-selected-quizzes', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }

  }


  async saveQuizzes() {
    if (this.lecture.id) {
      try {
        let list: number[] = [];
        for (let i = 0; i < this.selectedQuizzes.length; i++) {
          if(this.selectedQuizzes[i].id != null)
            list.push(this.selectedQuizzes[i].id)
        }
        console.log(list)
        this.lecture.quizzes = list;

      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  removeQuiz(quiz: StatementQuiz) {
    this.selectedQuizzes = this.selectedQuizzes.filter(
            element => element.id != quiz.id
    );
    this.saveQuizzes();
  }




  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

}
</script>
<style lang="scss" scoped>





</style>
