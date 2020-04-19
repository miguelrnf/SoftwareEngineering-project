<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            editSuggestion && editSuggestion.id === null
              ? 'New Suggestion'
              : 'Edit Suggestion'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editSuggestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <!--<v-flex xs24 sm12 md8>
              <v-text-field v-model="editSuggestion.title" label="Title" />
            </v-flex>-->
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editSuggestion._questionStr"
                label="Content"
                data-cy="content"
              ></v-textarea>
            </v-flex>
            <!--<v-flex
              xs24
              sm12
              md12
              v-for="index in editSuggestion.options.length"
              :key="index"
            >
              <v-switch
                v-model="editQuestion.options[index - 1].correct"
                class="ma-4"
                label="Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="editQuestion.options[index - 1].content"
                label="Content"
              ></v-textarea>
            </v-flex>-->
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-text>
        <v-autocomplete
                v-model="questionTopics"
                :items="topics"
                multiple
                return-object
                item-text="name"
                item-value="name"
                @change="saveTopics"

                data-cy="topics"
        >



          <template v-slot:selection="data">
            <v-chip
                    v-bind="data.attrs"
                    :input-value="data.selected"
                    close
                    @click="data.select"
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
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveSuggestion" data-cy="saveButton">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

</template>




<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import Suggestion from '@/models/management/Suggestion';
import Topic from '@/models/management/Topic';

@Component
export default class EditSuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Suggestion, required: true }) readonly suggestion!: Suggestion;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  editSuggestion!: Suggestion;

  questionTopics: Topic[] = JSON.parse(JSON.stringify(this.suggestion._topicsList));

  created() {
    /*if (
            this.suggestion &&
            (this.suggestion.status!='REJECTED')
    ) {
      this.$store.dispatch(
              'error',
              'You can only edit a rejected suggestion'
      );
      return;
    }*/

    this.editSuggestion = new Suggestion(this.suggestion);
  }

  // TODO use EasyMDE with these configs
  // markdownConfigs: object = {
  //   status: false,
  //   spellChecker: false,
  //   insertTexts: {
  //     image: ['![image][image]', '']
  //   }
  // };



    async saveSuggestion() {
    if (
      this.editSuggestion &&
      (!this.editSuggestion._questionStr)
    ) {
      await this.$store.dispatch(
        'error',
        'Suggestion must have content'
      );
      return;
    }

    if (this.editSuggestion && this.editSuggestion.id != null) {
      try {
        const result = await RemoteServices.updateSuggestion(this.editSuggestion);
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    } else if (this.editSuggestion) {
      try {
        this.editSuggestion._questionStr = this.editSuggestion._questionStr;
        this.editSuggestion._student = this.$store.getters.getUser;
        this.editSuggestion._topicsList = this.questionTopics;
        const result = await RemoteServices.createSuggestion(this.editSuggestion);
        this.$emit('save-suggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async saveTopics() {
    if (this.suggestion.id) {
      try {
        this.suggestion._topicsList = this.questionTopics;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    this.$emit(
            'question-changed-topics',
            this.suggestion.id,
            this.questionTopics
    );
  }

  removeTopic(topic: Topic) {
    this.questionTopics = this.questionTopics.filter(
            element => element.id != topic.id
    );
    this.saveTopics();
  }
}
</script>
